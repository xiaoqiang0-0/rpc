package com.xiaoqiang.rpc.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoqiang.rpc.RpcRequest;
import com.xiaoqiang.rpc.RpcResponse;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class RpcInvokeHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private ChannelHandlerContext context;
    private Map<Integer, CompletableFuture<RpcResponse>> futureMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
        super.channelActive(ctx);
    }

    public void sendRequest(RpcRequest request, CompletableFuture<RpcResponse> listener) {
        futureMap.put(request.getId(), listener);
    }

    public Future<RpcResponse> sendRequest(RpcRequest request){
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        futureMap.put(request.getId(), future);
        context.writeAndFlush(toByteBuf(request));
        return future;
    }

    private ByteBuf toByteBuf(RpcRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        ByteBuf result = Unpooled.buffer();
        try {
            byte[] data = mapper.writeValueAsBytes(request);
            result.writeBytes(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        RpcResponse response = toResponse(msg);
        CompletableFuture<RpcResponse> listener = futureMap.remove(response.getId());
        listener.complete(response);
    }

    private RpcResponse toResponse(ByteBuf msg) {
        ObjectMapper mapper = new ObjectMapper();
        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        return mapper.convertValue(new String(data, CharsetUtil.UTF_8), RpcResponse.class);
    }
}
