package com.dota2imca.portMapping;

import com.dota2imca.portMapping.util.BeanUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


public class TcpServer extends ServerAdapter {

    public static TcpServer newAndServe(ChannelInboundHandlerAdapter channelInboundHandlerAdapter,int port,boolean isCoder){
        return new TcpServer().serve(channelInboundHandlerAdapter,port,isCoder);
    }

    public static TcpServer newAndServe(ChannelInboundHandlerAdapter channelInboundHandlerAdapter,int port){
        return new TcpServer().serve(channelInboundHandlerAdapter,port,false);
    }

    public TcpServer serve(ChannelInboundHandlerAdapter channelInboundHandlerAdapter0,int port,final boolean isCoder) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup()
                ,workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        try {
            channel = new ServerBootstrap().group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            if (isCoder){
                                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(2048,0,4,0,4));
                                ch.pipeline().addLast(new LengthFieldPrepender(4));
                            }
                            ch.pipeline().addLast(BeanUtils.clone(channelInboundHandlerAdapter0));

                        }
                    }).bind(port).sync().channel();

            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this;
    }
}
