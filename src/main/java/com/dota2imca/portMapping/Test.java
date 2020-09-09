package com.dota2imca.portMapping;

import io.netty.channel.ChannelInboundHandlerAdapter;

public class Test {

    public static void main(String[] args) {
        TcpServer tcpServer = TcpServer.newAndServe(new ChannelInboundHandlerAdapter(), 8080);


    }

}
