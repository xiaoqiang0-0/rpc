package com.xiaoqiang.rpc.proxy;

public class Listener<T> {
    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void onFinished() {

    }

}
