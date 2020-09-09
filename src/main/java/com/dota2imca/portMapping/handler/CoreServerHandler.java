package com.dota2imca.portMapping.handler;

import com.dota2imca.portMapping.model.DataProtocol;
import com.dota2imca.portMapping.TcpServer;
import com.dota2imca.portMapping.UdpServer;
import com.dota2imca.portMapping.util.Util;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
public class CoreServerHandler extends ChannelInboundHandlerAdapter {

    private Channel userToServerChannel;

    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        Channel channel = channelHandlerContext.channel();
        DataProtocol data = DataProtocol.parse(msg);

        String body = data.getBody();

        String resBody = "";

        switch (data.getHead()) {
            //客户端连接
            case 1:


                InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
                log.info("{} register",inetSocketAddress.getHostString());

                resBody = body;

                break;
            //开启映射
            case 2:

                synchronized (this.getClass()){
                    int freePort = Util.getFreePort(30000,body);
                    if ("tcp".equals(body)){
                        userToServerChannel = TcpServer.newAndServe(new UserToServerTcpHandler().setCoreChannel(channel), freePort).getChannel();
                    }else if ("udp".equals(body)){
                        userToServerChannel = UdpServer.newAndServe(new UserToServerUdpHandler().setPort(freePort).setCoreChannel(channel), freePort).getChannel();
                    }else {
                        data.setHead((byte)(-data.getHead()));
                        data.setBody(data.getBody() + " is not support");
                    }


                    resBody = String.valueOf(freePort);
                }


                break;
        }

        channel.writeAndFlush(new DataProtocol(data.getHead(),resBody).toByteBuf());
        super.channelRead(channelHandlerContext,msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        userToServerChannel.close();

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        if (channel.remoteAddress() != null){
            InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
            log.info("{} connected",inetSocketAddress.getHostString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException){
            Channel channel = ctx.channel();
            if (channel.remoteAddress() != null){
                InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
                log.info("{} break",inetSocketAddress.getHostString());
            }
        }
        super.exceptionCaught(ctx, cause);
    }
}
