package com.dota2imca.portMapping.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Data
@Accessors(chain = true)
public abstract class UserToServerHandler extends ChannelInboundHandlerAdapter {

    protected Channel coreChannel;

    protected Channel channel;

    protected int port;


    protected CompletableFuture<Channel> serverToClientChannelFuture;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException){
            return;
        }
        super.exceptionCaught(ctx, cause);
    }
}
