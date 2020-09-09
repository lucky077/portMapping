package com.dota2imca.portMapping.model;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataProtocol {

    private byte head;

    private String body;

    public static DataProtocol parse(Object data){
        ByteBuf byteBuf = (ByteBuf) data;
        byte head = byteBuf.readByte();
        String body = byteBuf.toString(byteBuf.readerIndex(),byteBuf.readableBytes(), StandardCharsets.UTF_8);
        return new DataProtocol(head,body);
    }

    public static ByteBuf newByteBuf(int head,String body){
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = Unpooled.buffer(bodyBytes.length + 1);
        buffer.writeByte(head);
        buffer.writeBytes(bodyBytes);
        return buffer;
    }

    public ByteBuf toByteBuf(){
        byte[] bodyBytes = this.body.getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = Unpooled.buffer(bodyBytes.length + 1);
        buffer.writeByte(head);
        buffer.writeBytes(bodyBytes);
        return buffer;
    }

}
