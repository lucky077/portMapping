package com.dota2imca.portMapping;

import io.netty.channel.Channel;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;

@Getter
public abstract class ServerAdapter {

    protected Channel channel;

}
