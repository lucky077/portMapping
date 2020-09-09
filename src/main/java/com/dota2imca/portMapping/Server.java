package com.dota2imca.portMapping;

import com.dota2imca.portMapping.handler.CoreServerHandler;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {

    public static Channel channel;

    public static void main(String[] args) {

        log.info("start...");

        channel = TcpServer.newAndServe(new CoreServerHandler(),28999,true).channel;

    }


}
