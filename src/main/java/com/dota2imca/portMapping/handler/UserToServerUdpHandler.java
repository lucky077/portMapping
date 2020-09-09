package com.dota2imca.portMapping.handler;

import com.dota2imca.portMapping.model.DataProtocol;
import com.dota2imca.portMapping.UdpServer;
import com.dota2imca.portMapping.model.ServerToClientInfo;
import com.dota2imca.portMapping.util.Util;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Accessors(chain = true)
public class UserToServerUdpHandler extends UserToServerHandler{


    public static final Map<Integer,UserToServerUdpHandler> PORT_USER_TO_SERVER_MAP = new ConcurrentHashMap<>();
    public final Map<InetSocketAddress, ServerToClientInfo> ADDRESS_SERVER_TO_CLIENT_INFO_MAP = new ConcurrentHashMap<>();


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DatagramPacket packet = (DatagramPacket) msg;

        InetSocketAddress sender = packet.sender();

        ServerToClientInfo serverToClientInfo = ADDRESS_SERVER_TO_CLIENT_INFO_MAP.get(sender);

        if (serverToClientInfo == null){

            synchronized (this){
                Set<Integer> usedPorts = ADDRESS_SERVER_TO_CLIENT_INFO_MAP.values().stream().map(o -> o.getPort()).collect(Collectors.toSet());
                ServerToClientUdpHandler serverToClientUdpHandler = getFreePortByPool(usedPorts);
                int port;

                if (serverToClientUdpHandler != null){
                    port = serverToClientUdpHandler.getPort();
                }else {
                    synchronized (this.getClass()){

                        port = Util.getFreePort(40000,"udp");
                        UdpServer.newAndServe(new ServerToClientUdpHandler().setPort(port),port).getChannel();

                    }
                }

                ADDRESS_SERVER_TO_CLIENT_INFO_MAP.put(sender,serverToClientInfo = new ServerToClientInfo()
                        .setPort(port)
                        .setPortSelf(this.port)
                        .setAddressSelf(sender)
                );

                coreChannel.writeAndFlush(DataProtocol.newByteBuf((byte) 12,String.valueOf(port)));


            }

        }

        serverToClientInfo.getServerToClientChannelFuture().get(2, TimeUnit.SECONDS)
                .writeAndFlush(new DatagramPacket(packet.content().copy(),serverToClientInfo.getAddress()));

        super.channelRead(ctx, msg);
    }

    private ServerToClientUdpHandler getFreePortByPool(Set<Integer> usedPorts){
        for (ServerToClientUdpHandler serverToClientUdpHandler : ServerToClientUdpHandler.SERVER_TO_CLIENT_UDP_HANDLERS_POOL) {
            if ( ! usedPorts.contains(serverToClientUdpHandler.getPort())){
                return serverToClientUdpHandler;
            }
        }
        return null;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        PORT_USER_TO_SERVER_MAP.put(port,this);
        channel = ctx.channel();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        PORT_USER_TO_SERVER_MAP.remove(port,this);

        for (ServerToClientInfo serverToClientInfo : ADDRESS_SERVER_TO_CLIENT_INFO_MAP.values()) {
            ServerToClientUdpHandler serverToClientUdpHandler = serverToClientInfo.getServerToClientUdpHandler();
            if (serverToClientUdpHandler != null){
                serverToClientUdpHandler.ADDRESS_USER_TO_SERVER_INFO_MAP.remove(serverToClientInfo.getAddress());
            }
        }

        super.channelInactive(ctx);
    }

}
