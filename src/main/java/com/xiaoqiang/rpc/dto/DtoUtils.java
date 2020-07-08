package com.xiaoqiang.rpc.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DtoUtils {
    public static ByteBuf toByteBuf(RpcRequest request) throws JsonProcessingException {
        byte[] data = new ObjectMapper().writeValueAsBytes(request);
        return Unpooled.copiedBuffer(data);
    }

    public static ByteBuf toByteBuf(RpcResponse response) throws JsonProcessingException {
        byte[] data = new ObjectMapper().writeValueAsBytes(response);
        return Unpooled.copiedBuffer(data);
    }

    public static RpcResponse toResponse(ByteBuf msg) {
        byte[]data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        String jsonString = new String(data, UTF_8);
        try {
            return new ObjectMapper().readValue(jsonString, RpcResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static RpcRequest toRequest(ByteBuf msg) {
        byte[]data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        String jsonString = new String(data, UTF_8);
        try {
            return new ObjectMapper().readValue(jsonString, RpcRequest.class);
        } catch (Exception e) {
            return null;
        }
    }
}
