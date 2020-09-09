package com.dota2imca.portMapping.handler;

import com.dota2imca.portMapping.model.DataProtocol;
import com.dota2imca.portMapping.TcpServer;
import com.dota2imca.portMapping.util.Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class UserToServerTcpHandler extends UserToServerHandler{

    protected Channel serverChannel;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (serverToClientChannelFuture == null){
            synchronized (this){
                if (serverToClientChannelFuture == null){
                    serverToClientChannelFuture = new CompletableFuture<>();
                }
            }
        }

        serverToClientChannelFuture.get().writeAndFlush(Unpooled.copiedBuffer(((ByteBuf) msg)));
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        this.channel = ctx.channel();
        synchronized (this.getClass()){
            port = Util.getFreePort(40000,"tcp");
            serverChannel = TcpServer.newAndServe(new ServerToClientTcpHandler().setUserToServerHandler(this),port).getChannel();
        }


        coreChannel.writeAndFlush(DataProtocol.newByteBuf((byte) 11,String.valueOf(port)));

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {


        coreChannel.writeAndFlush(DataProtocol.newByteBuf((byte) 21,String.valueOf(port)));

        serverChannel.close();

        super.channelInactive(ctx);
    }

}
