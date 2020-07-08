package com.xiaoqiang.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RpcServer {
    private final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private final ConcurrentMap<String, Object> servicePool = new ConcurrentHashMap<>();
    private final RpcServerTransactionHandler transactionHandler;
    private final NioEventLoopGroup executors;
    private final ServerBootstrap serverBootstrap;
    private final String host;
    private final int port;
    private ChannelFuture successStartFuture;

    public RpcServer(int port) {
        this("127.0.0.1", port);
    }

    public RpcServer(String host, int port) {
        transactionHandler = new RpcServerTransactionHandler(servicePool::get);
        this.executors = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(executors);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(transactionHandler);
            }
        });
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        this.successStartFuture = serverBootstrap.bind(host, port).sync();
        this.successStartFuture.addListener(future -> {
            if (future.isSuccess()) {
                logger.info("server start...");
            } else {
                logger.error("server start failed!");
            }
        });
    }

    public void close() {
        try {
            executors.shutdownGracefully().sync().addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("Server is shutdown!");
                } else {
                    logger.error("Server shutdown failed!");
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ConcurrentMap<String, Object> getServicePool() {
        return servicePool;
    }

    public void register(String name, Object service) {
        servicePool.put(name, service);
    }

    public void register(Class<?> serviceClass, Object service) {
        servicePool.put(serviceClass.getName(), service);
    }
}
