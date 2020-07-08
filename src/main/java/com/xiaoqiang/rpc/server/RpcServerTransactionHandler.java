package com.xiaoqiang.rpc.server;

import com.xiaoqiang.rpc.dto.RpcRequest;
import com.xiaoqiang.rpc.dto.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.function.Function;

import static com.xiaoqiang.rpc.dto.DtoUtils.toByteBuf;
import static com.xiaoqiang.rpc.dto.DtoUtils.toRequest;

@ChannelHandler.Sharable
public class RpcServerTransactionHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final Function<String, Object> serviceFinder;

    public RpcServerTransactionHandler(Function<String, Object> serviceFinder) {
        this.serviceFinder = serviceFinder;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        RpcRequest request = toRequest(msg);
        System.out.printf("收到来自客户端的消息：[%s]\n", request);
        if (request != null) {
            RpcResponse response = doRequest(request);
            System.out.printf("服务端处理结果：[%s]\n", response);
            ctx.writeAndFlush(toByteBuf(response));
        }
    }

    private RpcResponse doRequest(RpcRequest request) {
        Object service = serviceFinder.apply(request.getService());
        RpcResponse response = new RpcResponse();
        response.setId(request.getId());
        try {
            Class<?>[] argTypes = new Class[request.getArgs().length];
            for (int i = 0; i < request.getArgs().length; i++) {
                argTypes[i] = request.getArgs()[i].getClass();
            }
            Method method = service.getClass().getMethod(request.getMethodName(), argTypes);
            Object result = method.invoke(service, request.getArgs());
            response.setResult(result);
            response.setState(1);
        } catch (Exception e) {
            response.setState(-1);
            response.setMsg(e.getMessage());
        }
        return response;
    }
}
