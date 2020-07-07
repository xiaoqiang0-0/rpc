package com.xiaoqiang.rpc.proxy;

import com.xiaoqiang.rpc.RpcRequest;
import com.xiaoqiang.rpc.RpcResponse;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RpcServer {
    private static final RpcServer instance = new RpcServer();
    private final ConcurrentMap<String, Object> servicePool = new ConcurrentHashMap<>();

    private RpcServer() {
    }

    public static RpcServer getInstance() {
        return instance;
    }

    public void register(String service, Object target) {
        servicePool.put(service, target);
    }

    public RpcResponse invoke(RpcRequest request) {
        Object target = servicePool.get(request.getService());
        RpcResponse response = new RpcResponse();
        try {
            Method method = target.getClass().getMethod(request.getMethodName());
            Object result = method.invoke(target, request.getArgs());
            response.setState(1);
            response.setResult(result);
        }catch (Exception e){
            response.setState(-1);
            response.setMsg(e.getMessage());
        }

        return response;
    }

    public static void main(String[] args) {

    }
}
