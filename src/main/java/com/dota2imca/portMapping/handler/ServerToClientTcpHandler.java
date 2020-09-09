package com.dota2imca.portMapping.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletableFuture;

@Data
@Accessors(chain = true)
public class ServerToClientTcpHandler extends ServerToClientHandler {

    protected UserToServerTcpHandler userToServerHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        userToServerHandler.channel.writeAndFlush(Unpooled.copiedBuffer(((ByteBuf) msg)));
        super.channelRead(ctx, msg);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        if (userToServerHandler.getServerToClientChannelFuture() == null){
            synchronized (userToServerHandler){
                if (userToServerHandler.getServerToClientChannelFuture() == null){
                    userToServerHandler.setServerToClientChannelFuture(new CompletableFuture<>());
                }
            }
        }

        userToServerHandler.getServerToClientChannelFuture().complete(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        userToServerHandler = null;
        super.channelInactive(ctx);
    }


}
