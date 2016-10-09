package org.wenxueliu.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.channels.AsynchronousChannelGroup;

public class EchoAioServer {
    private AsynchronousServerSocketChannel server;
    private int port = 9000;

    public EchoAioServer(int port) throws IOException {
        this.port = port;
        ExecutorService executorService = Executors.newCachedThreadPool();
        AsynchronousChannelGroup threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
        server = AsynchronousServerSocketChannel.open().bind(
                new InetSocketAddress(port));
        System.out.println("Server listen on " + this.port);
    }

    public void startWithFuture() throws InterruptedException,
            ExecutionException, TimeoutException {
        Future<AsynchronousSocketChannel> future = server.accept();
        AsynchronousSocketChannel socket = future.get();
        ByteBuffer readBuf = ByteBuffer.allocate(1024);
        readBuf.clear();
        socket.read(readBuf).get(100, TimeUnit.SECONDS);
        readBuf.flip();
        socket.write(readBuf).get(100, TimeUnit.SECONDS);
        readBuf.flip();
        System.out.printf("received message:" + new String(readBuf.array()));
        System.out.println(Thread.currentThread().getName());
    }

    public void startWithCompletionHandler() throws InterruptedException,
            ExecutionException, TimeoutException {
        //注册事件和事件完成后的处理器
        server.accept(null,
                new CompletionHandler<AsynchronousSocketChannel, Object>() {
                    final ByteBuffer buffer = ByteBuffer.allocate(1024);

                    public void completed(AsynchronousSocketChannel result,
                            Object attachment) {
                        System.out.println(Thread.currentThread().getName());
                        System.out.println("start");
                        try {
                            buffer.clear();
                            result.read(buffer).get(100, TimeUnit.SECONDS);
                            buffer.flip();
                            System.out.println("received message: "
                                    + new String(buffer.array()));
                            result.write(buffer);
                            buffer.flip();
                        } catch (InterruptedException | ExecutionException e) {
                            System.out.println(e.toString());
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        } finally {

                            try {
                                result.close();
                                server.accept(null, this);
                            } catch (Exception e) {
                                System.out.println(e.toString());
                            }
                        }

                        System.out.println("end");
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        System.out.println("failed: " + exc);
                    }
                });
        // 主线程继续自己的行为
        while (true) {
            System.out.println("main thread");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String args[]) throws Exception {
        new EchoAioServer(9000).startWithCompletionHandler();
    }
}
