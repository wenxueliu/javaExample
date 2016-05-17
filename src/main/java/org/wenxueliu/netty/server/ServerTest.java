package org.wenxueliu.netty.server;

import java.util.HashMap;
import java.util.List;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.LinkedTransferQueue;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.Timer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerTest {

    protected static final Logger logger =
            LoggerFactory.getLogger(ServerTest.class);

    /**
     * Channel group that will hold all our channels
     */
    private final ChannelGroup cg = new DefaultChannelGroup("Internal RPC", GlobalEventExecutor.INSTANCE);

    /**
     * {@link EventLoopGroup} used for netty boss threads
     */
    protected EventLoopGroup bossGroup;

    /**
     * {@link EventLoopGroup} used for netty worker threads
     */
    protected EventLoopGroup workerGroup;

    /**
     * Netty {@link ClientBootstrap} used for creating client connections 
     */
    protected Bootstrap clientBootstrap;

    /**
     * {@link ServerChannelInitializer} for creating connections 
     */
    protected ServerChannelInitializer channelInitializer;

    /**
     * Node connections
     */
    protected HashMap<Short, NodeConnection> connections = 
            new HashMap<Short, NodeConnection>();

    protected List<Node> nodes;

    /**
     * Buffer size for sockets
     */
    public static final int SEND_BUFFER_SIZE = 4 * 1024 * 1024;

    /**
     * Connect timeout for client connections
     */
    public static final int CONNECT_TIMEOUT = 500;

    /**
     * True after the {@link ServerTest#run()} method is called
     */
    protected boolean started = false;

    /**
     * true after the {@link RPCService#shutdown()} method
     * is called.
     */
    protected volatile boolean shutDown = false;

    /**
     * Timer used for timeouts
     */
    private final Timer timer;

    /**
     * If we want to rate-limit certain types of messages, we can do
     * so by limiting the overall number of outstanding messages.
     * The number of such messages will be stored in the
     * {@link MessageWindow}
     */
    protected ConcurrentHashMap<Short, MessageWindow> messageWindows;

    /**
     * A thread pool for handling sync messages.  These messages require
     * a separate pool since writing to the node can be a blocking operation
     * while waiting for window capacity, and blocking the I/O threads could
     * lead to deadlock
     * @see SyncMessageWorker
     */
    protected ExecutorService syncExecutor;

    /**
     * Number of workers in the sync message thread pool
     */
    protected static final int SYNC_MESSAGE_POOL = 2;

    /**
     * The maximum number of outstanding pending messages for messages
     * that use message windows
     */
    protected static final int MAX_PENDING_MESSAGES = 500;

    public ServerTest(Timer timer) {
        this.timer = timer;
        messageWindows = new ConcurrentHashMap<Short, MessageWindow>();
    }
 
    public String getLocalNodeId() {
        return "127.0.0.1";
    }

    public List<Node> getNodes() {
        return this.nodes;
    }

    // *************
    // public methods
    // *************

    /**
     * Start the RPC service
     */
    public void run(String ip, int port) {
        started = true;

        final ThreadGroup tg2 = new ThreadGroup("Sync I/O Threads");
        tg2.setMaxPriority(Thread.NORM_PRIORITY - 1);
        ThreadFactory f2 = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(tg2, runnable);
            }
        };

        bossGroup = new NioEventLoopGroup(0, f2);
        workerGroup = new NioEventLoopGroup(0, f2);

        channelInitializer = new ServerChannelInitializer(this, timer);

        startServer(channelInitializer, ip, port);
        startClients(channelInitializer);
    }

    /**
     * Stop the RPC service
     */
    public void shutdown() {
        shutDown = true;
        try {
            if (!cg.close().await(5, TimeUnit.SECONDS)) {
                logger.warn("Failed to cleanly shut down RPC server");
                return;
            }

            clientBootstrap = null;
            channelInitializer = null;
            if (bossGroup != null)
            	bossGroup.shutdownGracefully();
            bossGroup = null;
            if (workerGroup != null)
            	workerGroup.shutdownGracefully();
            workerGroup = null;
        } catch (InterruptedException e) {
            logger.warn("Interrupted while shutting down RPC server");
        }
        logger.debug("Internal floodlight RPC shut down");
    }

    /**
     * Remove the connection from the connection registry and clean up
     * any remaining shrapnel
     * @param nodeId
     */
    public void disconnectNode(short nodeId) {
        synchronized (connections) {
            Short n = Short.valueOf(nodeId);
            MessageWindow mw = messageWindows.get(n);
            if (mw != null) {
                mw.lock.lock();
                mw.disconnected = true;
                try {
                    mw.full.signalAll();
                    messageWindows.remove(n);
                } finally {
                    mw.lock.unlock();
                }
            }

            NodeConnection nc = connections.get(nodeId);
            if (nc != null) {
                nc.nuke();
            }
            connections.remove(nodeId);
        }
    }

    /**
     * Find out if a particular node is connected
     * @param nodeId
     * @return true if the node is connected
     */
    public boolean isConnected(short nodeId) {
        NodeConnection nc = connections.get(nodeId);
        return (nc != null && nc.state == NodeConnectionState.CONNECTED);
    }

    /**
     * Called when a message is acknowledged by a remote node
     * @param type the message type
     * @param nodeId the remote node
     */
    //public void messageAcked(MessageType type, Short nodeId) {
    //    if (nodeId == null) return;
    //    if (!windowedTypes.contains(type)) return;

    //    MessageWindow mw = messageWindows.get(nodeId);
    //    if (mw == null) return;

    //    int pending = mw.pending.decrementAndGet();
    //    if (pending < MAX_PENDING_MESSAGES) {
    //        mw.lock.lock();
    //        try {
    //            mw.full.signalAll();
    //        } finally {
    //            mw.lock.unlock();
    //        }
    //    }
    //}

    /**
     * Wait for a message window slow to be available for the given node and 
     * message type
     * @param type the type of the message
     * @param nodeId the node Id
     * @param maxWait the maximum time to wait in milliseconds
     * @throws InterruptedException 
     * @return <code>true</code> if the message can be safely written
     */
    //private boolean waitForMessageWindow(MessageType type, short nodeId,
    //                                     long maxWait) 
    //        throws InterruptedException {
    //    if (!windowedTypes.contains(type)) return true;

    //    long start = System.nanoTime();
    //
    //    // note that this can allow slightly more than the maximum number
    //    // of messages.  This is fine.
    //    MessageWindow mw = getMW(nodeId);
    //    if (!mw.disconnected &&
    //        mw.pending.get() >= MAX_PENDING_MESSAGES) {
    //        mw.lock.lock();
    //        try {
    //            while (!mw.disconnected &&
    //                   mw.pending.get() >= MAX_PENDING_MESSAGES) {
    //                long now = System.nanoTime();
    //                if (maxWait > 0 &&
    //                    (now - start) > maxWait * 1000) return false;
    //                mw.full.awaitNanos(now - start);
    //            }
    //        } finally {
    //            mw.lock.unlock();
    //        }
    //    }
    //    mw = getMW(nodeId);
    //    if (mw != null)
    //        mw.pending.getAndIncrement();
    //
    //    return true;
    //}

    /**
     * Start listening sockets
     */
    protected void startServer(ServerChannelInitializer channelInitializer, String listenAddress, int port) {
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 128)
        .option(ChannelOption.SO_REUSEADDR, true)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.SO_SNDBUF, SEND_BUFFER_SIZE)
        .option(ChannelOption.SO_RCVBUF, SEND_BUFFER_SIZE)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT)
        .childHandler(channelInitializer);


        InetSocketAddress sa;
        if (listenAddress != null)
            sa = new InetSocketAddress(listenAddress, port);
        else
            sa = new InetSocketAddress(port);

        ChannelFuture bindFuture = bootstrap.bind(sa);
        cg.add(bindFuture.channel());

        logger.info("Listening for internal floodlight RPC on {}", sa);
    }

    /**
     * Wait for the client connection
     * @author readams
     */
    protected class ConnectCFListener implements ChannelFutureListener {
        protected Node node;

        public ConnectCFListener(Node node) {
            super();
            this.node = node;
        }

        @Override
        public void operationComplete(ChannelFuture cf) throws Exception {
            if (!cf.isSuccess()) {
                synchronized (connections) {
                    NodeConnection c = connections.remove(node.getNodeId());
                    if (c != null) c.nuke();
                    cf.channel().close();
                }

                String message = "[unknown error]";
                if (cf.isCancelled()) message = "Timed out on connect";
                if (cf.cause() != null) message = cf.cause().getMessage();
                logger.debug("[{}->{}] Could not connect to RPC " +
                             "node: {}", 
                             new Object[]{getLocalNodeId(), 
                                          node.getNodeId(), 
                                          message});
            } else {
                logger.trace("[{}->{}] Channel future successful", 
                             getLocalNodeId(), 
                             node.getNodeId());
            }
        }
    }

    /**
     * Add the node connection to the node connection map
     * @param nodeId the node ID for the channel
     * @param channel the new channel
     */
    protected void nodeConnected(short nodeId, Channel channel) {
        logger.debug("[{}->{}] Connection established",
                     getLocalNodeId(),
                     nodeId);
        synchronized (connections) {
            NodeConnection c = connections.get(nodeId);
            if (c == null) {
                connections.put(nodeId, c = new NodeConnection());
            }
            c.nodeChannel = channel;
            c.state = NodeConnectionState.CONNECTED;
        }
    }

    /**
     * Connect to remote servers.  We'll initiate the connection to
     * any nodes with a lower ID so that there will be a single connection
     * between each pair of nodes which we'll use symmetrically
     */
    protected void startClients(ServerChannelInitializer channelInitializer) {
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_REUSEADDR, true)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.SO_SNDBUF, SEND_BUFFER_SIZE)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT)
        .handler(channelInitializer);
        clientBootstrap = bootstrap;

        //ScheduledExecutorService ses = 
        //        syncManager.getThreadPool().getScheduledExecutor();
        //reconnectTask = new SingletonTask(ses, new ConnectTask());
        //reconnectTask.reschedule(0, TimeUnit.SECONDS);
    }

    /**
     * Connect to a remote node if appropriate
     * @param bootstrap the client bootstrap object
     * @param n the node to connect to
     */
    protected void doNodeConnect(Node n) {
        //if (!shutDown && n.getNodeId() < getLocalNodeId()) {
        if (!shutDown) {
            Short nodeId = n.getNodeId();

            synchronized (connections) {
                NodeConnection c = connections.get(n.getNodeId());
                if (c == null) {
                    connections.put(nodeId, c = new NodeConnection());
                }

                if (logger.isTraceEnabled()) {
                    logger.trace("[{}->{}] Connection state: {}", 
                                 new Object[]{getLocalNodeId(),
                                              nodeId, c.state});
                }
                if (c.state.equals(NodeConnectionState.NONE)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[{}->{}] Attempting connection {} {}", 
                                     new Object[]{getLocalNodeId(),
                                                  nodeId, 
                                                  n.getHostname(), 
                                                  n.getPort()});
                    }
                    SocketAddress sa =
                            new InetSocketAddress(n.getHostname(), n.getPort());
                    c.pendingFuture = clientBootstrap.connect(sa);
                    c.pendingFuture.addListener(new ConnectCFListener(n));
                    c.state = NodeConnectionState.PENDING;
                }
            }
        }
    }

    /**
     * Ensure that all client connections are active
     */
    protected void startClientConnections() {
        for (Node n : getNodes()) {
            doNodeConnect(n);
        }        
    }

    /**
     * Retrieve the Netty ChannelGroup
     * @return
     */
    protected ChannelGroup getChannelGroup() {
    	return cg;
    }

    /**
     * Periodically ensure that all the node connections are alive
     * @author readams
     */
    protected class ConnectTask implements Runnable {
        @Override
        public void run() {
            try {
                if (!shutDown)
                    startClientConnections();
            } catch (Exception e) {
                logger.error("Error in reconnect task", e);
            }
            if (!shutDown) {
                //reconnectTask.reschedule(500, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * Various states for connections
     * @author readams
     */
    protected enum NodeConnectionState {
        NONE,
        PENDING,
        CONNECTED
    }

    /**
     * Connection state wrapper for node connections
     * @author readams
     */
    protected static class NodeConnection {
        volatile NodeConnectionState state = NodeConnectionState.NONE;        
        protected ChannelFuture pendingFuture;
        protected Channel nodeChannel;
        
        protected void nuke() {
            state = NodeConnectionState.NONE;
            if (pendingFuture != null) pendingFuture.cancel(false);
            if (nodeChannel != null) nodeChannel.close();
            pendingFuture = null;
            nodeChannel = null;
        }
    }

    /**
     * Maintain state for the pending message window for a given message type
     * @author readams
     */
    protected static class MessageWindow {
        AtomicInteger pending = new AtomicInteger();
        volatile boolean disconnected = false;
        Lock lock = new ReentrantLock();
        Condition full = lock.newCondition();
    }

    /**
     * A pending message to be sent to a particular mode.
     * @author readams
     */
    //protected static class NodeMessage extends Pair<Short,SyncMessage> {
    //    private static final long serialVersionUID = -3443080461324647922L;

    //    public NodeMessage(Short first, SyncMessage second) {
    //        super(first, second);
    //    }
    //}
    
    /**
     * A worker thread responsible for reading sync messages off the queue
     * and writing them to the appropriate node's channel.  Because calls 
     * {@link RPCService#writeToNode(Short, SyncMessage)} can block while
     * waiting for available slots in the message window, we do this in a
     * separate thread.
     * @author readams
     */
    //protected class SyncMessageWorker implements Runnable {
    //    @Override
    //    public void run() {
    //        while (true) {
    //            try {
    //                NodeMessage m = syncQueue.take();
    //                writeToNode(m.getFirst(), m.getSecond());
    //            } catch (Exception e) {
    //                logger.error("Error while dispatching message", e);
    //            }
    //        }
    //    }
    //}

}
