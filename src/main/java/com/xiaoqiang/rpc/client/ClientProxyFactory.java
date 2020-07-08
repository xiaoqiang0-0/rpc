package com.xiaoqiang.rpc.client;

import java.lang.reflect.Proxy;

public class ClientProxyFactory {
    public static <T> T get(Class<T> c, RpcClientTransactionHandler transactionHandler) {
        return (T) Proxy.newProxyInstance(RpcClient.class.getClassLoader(), new Class[]{c}, new RpcProxyHandler(transactionHandler, c.getName()));
    }
}
