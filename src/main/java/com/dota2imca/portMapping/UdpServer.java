package com.dota2imca.portMapping;

import com.dota2imca.portMapping.util.BeanUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UdpServer extends ServerAdapter {

    public static UdpServer newAndServe(ChannelInboundHandlerAdapter channelInboundHandlerAdapter, int port){
        return new UdpServer().serve(channelInboundHandlerAdapter,port);
    }

    public UdpServer serve(ChannelInboundHandlerAdapter channelInboundHandlerAdapter0,int port) {

        NioEventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        try {
            channel =  new Bootstrap().group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(BeanUtils.clone(channelInboundHandlerAdapter0)).bind(port).sync().channel();

            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                group.shutdownGracefully();
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return this;
    }
}
