package com.xiaoqiang.rpc.client;

import com.xiaoqiang.rpc.dto.RpcRequest;
import com.xiaoqiang.rpc.dto.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RpcProxyHandler implements InvocationHandler {

    private final RpcClientTransactionHandler transactionHandler;
    private final String serviceName;

    public RpcProxyHandler(RpcClientTransactionHandler handler, String serviceName) {
        this.transactionHandler = handler;
        this.serviceName = serviceName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setService(serviceName);
        request.setMethodName(method.getName());
        request.setArgTypes(method.getParameterTypes());
        request.setArgs(args);
        CompletableFuture<RpcResponse> future = transactionHandler.sendRpcRequest(request);
        RpcResponse response = future.get(10, TimeUnit.SECONDS);
        if (response.getState()!=1) {
            throw new RuntimeException(response.getMsg());
        }
        return response.getResult();
    }
}
