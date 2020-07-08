package com.xiaoqiang.rpc.server;

import com.xiaoqiang.rpc.dto.RpcRequest;
import com.xiaoqiang.rpc.dto.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.function.Function;

import static com.xiaoqiang.rpc.dto.DtoUtils.toByteBuf;
import static com.xiaoqiang.rpc.dto.DtoUtils.toRequest;

@ChannelHandler.Sharable
public class RpcServerTransactionHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final Logger logger = LoggerFactory.getLogger(RpcServerTransactionHandler.class);

    private final Function<String, Object> serviceFinder;

    public RpcServerTransactionHandler(Function<String, Object> serviceFinder) {
        this.serviceFinder = serviceFinder;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        RpcRequest request = toRequest(msg);
        logger.debug("收到来自客户端的消息：[{}]", request);
        if (request != null) {
            RpcResponse response = doRequest(request);
            logger.debug("服务端处理结果：[{}]", response);
            ctx.writeAndFlush(toByteBuf(response));
        }
    }

    private RpcResponse doRequest(RpcRequest request) {
        Object service = serviceFinder.apply(request.getService());
        RpcResponse response = new RpcResponse();
        response.setId(request.getId());
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getArgTypes());
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
