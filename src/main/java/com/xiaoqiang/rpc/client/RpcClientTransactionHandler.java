package com.xiaoqiang.rpc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xiaoqiang.rpc.dto.DtoUtils;
import com.xiaoqiang.rpc.dto.RpcRequest;
import com.xiaoqiang.rpc.dto.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class RpcClientTransactionHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ChannelHandlerContext context;
    private final ConcurrentMap<Integer, CompletableFuture<RpcResponse>> futureConcurrentMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.context = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        RpcResponse response = DtoUtils.toResponse(msg);
        if (response != null) {
            CompletableFuture<RpcResponse> future = futureConcurrentMap.remove(response.getId());
            future.complete(response);
        }
    }

    public CompletableFuture<RpcResponse> sendRpcRequest(RpcRequest request) throws JsonProcessingException {

        System.out.printf("发送消息至服务端：[%s]\n", request);
        context.writeAndFlush(DtoUtils.toByteBuf(request));
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        futureConcurrentMap.put(request.getId(), future);
        return future;
    }

}
