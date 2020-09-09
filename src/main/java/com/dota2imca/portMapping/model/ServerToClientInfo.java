package com.dota2imca.portMapping.model;

import com.dota2imca.portMapping.handler.ServerToClientUdpHandler;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

@Data
@Accessors(chain = true)
public class ServerToClientInfo {

    private InetSocketAddress address;

    private InetSocketAddress addressSelf;

    private int port;

    private int portSelf;

    private ServerToClientUdpHandler serverToClientUdpHandler;

    private CompletableFuture<Channel> serverToClientChannelFuture = new CompletableFuture<>();

}
