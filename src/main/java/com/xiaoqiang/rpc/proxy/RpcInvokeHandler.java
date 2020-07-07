package com.xiaoqiang.rpc.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoqiang.rpc.RpcRequest;
import com.xiaoqiang.rpc.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcInvokeHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private ChannelHandlerContext context;
    private Map<Integer, Listener<RpcResponse>> listenerMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
        super.channelActive(ctx);
    }

    public void sendRequest(RpcRequest request, Listener<RpcResponse> listener) {
        listenerMap.put(request.getId(), listener);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        RpcResponse response = toResponse(msg);
        Listener<RpcResponse> listener = listenerMap.remove(response.getId());
        listener.setResult(response);
        listener.onFinished();
    }

    private RpcResponse toResponse(ByteBuf msg) {
        ObjectMapper mapper = new ObjectMapper();
        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        return mapper.convertValue(new String(data, CharsetUtil.UTF_8), RpcResponse.class);
    }
}
