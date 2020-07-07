package com.xiaoqiang.rpc.proxy;

import com.xiaoqiang.rpc.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxyHandler implements InvocationHandler {

    private final String service;

    public RpcProxyHandler(String service) {
        this.service = service;
    }

    public static <T>T proxy(Class<T> c){
        return (T)Proxy.newProxyInstance(RpcProxyHandler.class.getClassLoader(), new Class[]{c}, new RpcProxyHandler(c.getName()));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setService(service);
        request.setMethodName(method.getName());
        request.setArgs(args);

        return null;
    }
}
