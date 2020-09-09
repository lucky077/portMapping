package com.dota2imca.portMapping.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.IOException;

@Data
@Accessors(chain = true)
public abstract class ServerToClientHandler extends ChannelInboundHandlerAdapter {



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException){
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
}
