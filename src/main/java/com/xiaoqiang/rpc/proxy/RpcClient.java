package com.xiaoqiang.rpc.proxy;

import com.xiaoqiang.rpc.RpcRequest;
import com.xiaoqiang.rpc.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient {
    private EventLoopGroup executors;
    private Bootstrap bootstrap;

    public RpcClient(String host, int port) {
        executors = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(executors);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(host, port);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RpcInvokeHandler());
            }
        });
        initConnect();
    }

    public void initConnect() {
        try {
            bootstrap.connect().sync().channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            executors.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public RpcResponse rpcInvoke(RpcRequest request) {

        return null;
    }
}
