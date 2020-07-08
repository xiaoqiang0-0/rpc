package com.xiaoqiang.rpc.dto;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class RpcRequest {
    public static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private int id;
    private String service;
    private String methodName;
    private Object[] args;

    public RpcRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "id=" + id +
                ", service='" + service + '\'' +
                ", methodName='" + methodName + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
