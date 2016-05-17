package org.wenxueliu.netty.server;

import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.net.ConnectException;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerChannelHandler extends ChannelInboundHandlerAdapter {
    protected static final Logger logger =
            LoggerFactory.getLogger(ServerChannelHandler.class);

    protected boolean isClientConnection = false;

    protected enum ChannelState {
        OPEN,
        CONNECTED,
        AUTHENTICATED;
    }

    protected ChannelState channelState = ChannelState.OPEN;

    public ServerChannelHandler() {
        super();
    }

    // ****************************
    // IdleStateAwareChannelHandler
    // ****************************

    void timeServer(final ChannelHandlerContext ctx) {
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        final ChannelFuture f = ctx.writeAndFlush(time);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                ctx.close();
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        isClientConnection = true;
        channelState = ChannelState.CONNECTED;
        logger.info("channel active");
        //msg = new Message();
        //ctx.channel().writeAndFlush(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    	if (evt instanceof IdleStateEvent) {
    		channelIdle(ctx, (IdleStateEvent) evt);
    	}
    	super.userEventTriggered(ctx, evt);
    }

    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
        // send an echo request
        //EchoRequestMessage m = new EchoRequestMessage();
        //AsyncMessageHeader header = new AsyncMessageHeader();
        //header.setTransactionId(getTransactionId());
        //m.setHeader(header);
        //SyncMessage bsm = new SyncMessage(MessageType.ECHO_REQUEST);
        //bsm.setEchoRequest(m);
        //ctx.channel().writeAndFlush(bsm);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            // read timeout
            logger.error("[{}->{}] Disconnecting RPC node due to read timeout",
                         getLocalNodeIdString(), getRemoteNodeIdString());
            ctx.channel().close();
        } else if (cause instanceof ConnectException ||
        		cause instanceof IOException) {
            logger.debug("[{}->{}] {}: {}", 
                         new Object[] {getLocalNodeIdString(),
                                       getRemoteNodeIdString(), 
                                       cause.getClass().getName(),
                                       cause.getMessage()});
        } else {
            logger.error("[{}->{}] An error occurred on RPC channel",
                         new Object[]{getLocalNodeIdString(), 
                                      getRemoteNodeIdString(),
                                      cause});
            ctx.channel().close();
        }
    }

    private void discard(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) {
                System.out.print((char) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void echo(ChannelHandlerContext ctx, Object message) {
        ctx.writeAndFlush(message);
    }

    ///**
    // * Handle a generic {@link SyncMessage} and dispatch to an appropriate
    // * handler
    // * @param bsm the message
    // * @param channel the channel on which the message arrived
    // */
    //protected void handleSyncMessage(SyncMessage bsm, Channel channel) {
    //    switch (channelState) {
    //        case OPEN:
    //        case CONNECTED:
    //            switch (bsm.getType()) {
    //                case HELLO:
    //                    handshake(bsm.getHello(), channel);
    //                    break;
    //                case ECHO_REQUEST:
    //                    handleEchoRequest(bsm.getEchoRequest(), channel);
    //                    break;
    //                case ERROR:
    //                    handleError(bsm.getError(), channel);
    //                    break;
    //                default:
    //                    // ignore
    //            }
    //            break;
    //        case AUTHENTICATED:
    //            handleSMAuthenticated(bsm, channel);
    //            break;
    //    }
    //}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        logger.info("channel read");
        isClientConnection = true;
        //if (message instanceof SyncMessage) {
        //    handleSyncMessage((SyncMessage)message, ctx.channel());
        //} else if (message instanceof List) {
        //    for (Object i : (List<?>)message) {
        //        if (i instanceof SyncMessage) {
        //            try {
        //                handleSyncMessage((SyncMessage)i,
        //                                     ctx.channel());
        //            } catch (Exception ex) {
        //                ctx.fireExceptionCaught(ex);
        //            }
        //        }
        //    }
        //} else {
        //    handleUnknownMessage(ctx, message);
        //}
    }

    // ****************
    // Message Handlers
    // ****************

    /**
     * A handler for messages on the channel that are not of type 
     * {@link SyncMessage}
     * @param ctx the context
     * @param message the message object
     */
    protected void handleUnknownMessage(ChannelHandlerContext ctx, 
                                        Object message) {
        logger.warn("[{}->{}] Unhandled message: {}", 
                    new Object[]{getLocalNodeIdString(), 
                                 getRemoteNodeIdString(),
                                 message.getClass().getCanonicalName()});
    }

    /**
     * Get the node ID for the remote node if its connected
     * @return the node ID
     */
    protected Short getRemoteNodeId() {
        return 2;
    }

    /**
     * Get the node ID for the remote node if its connected as a string
     * for use output
     * @return the node ID
     */
    protected String getRemoteNodeIdString() {
        return ""+getRemoteNodeId();
    }

    protected Short getLocalNodeId() {
        return 1;
    }

    /**
     * Get the node ID for the local node as a string for use output
     * @return the node ID
     */
    protected String getLocalNodeIdString() {
        return ""+getLocalNodeId();
    }
}


