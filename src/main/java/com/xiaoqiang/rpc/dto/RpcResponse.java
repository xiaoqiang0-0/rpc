package com.xiaoqiang.rpc.dto;


public class RpcResponse {
    private int id;
    private int state;
    private Object result;
    private String msg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "id=" + id +
                ", state=" + state +
                ", result=" + result +
                ", msg='" + msg + '\'' +
                '}';
    }
}
