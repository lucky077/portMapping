package com.dota2imca.portMapping.handler;

import com.dota2imca.portMapping.model.ServerToClientInfo;
import com.dota2imca.portMapping.model.UserToServerInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class ServerToClientUdpHandler extends ServerToClientHandler {

    private int port;

    public static final List<ServerToClientUdpHandler> SERVER_TO_CLIENT_UDP_HANDLERS_POOL = new CopyOnWriteArrayList<>();

    public final Map<InetSocketAddress, UserToServerInfo> ADDRESS_USER_TO_SERVER_INFO_MAP = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        DatagramPacket packet = (DatagramPacket) msg;

        InetSocketAddress sender = packet.sender();

        UserToServerInfo userToServerInfo = ADDRESS_USER_TO_SERVER_INFO_MAP.get(sender);

        if (userToServerInfo == null){
            Integer port = Integer.valueOf(packet.content().toString(StandardCharsets.UTF_8));

            UserToServerUdpHandler userToServerUdpHandler = UserToServerUdpHandler.PORT_USER_TO_SERVER_MAP.get(port);



            ServerToClientInfo serverToClientInfo = userToServerUdpHandler.ADDRESS_SERVER_TO_CLIENT_INFO_MAP.values().stream()
                    .filter(o -> Objects.equals(this.port,o.getPort())).collect(Collectors.toList()).get(0);

            ADDRESS_USER_TO_SERVER_INFO_MAP.put(sender,new UserToServerInfo()
                .setAddress(serverToClientInfo.getAddressSelf())
                    .setUserToServerUdpHandler(userToServerUdpHandler)
            );

            serverToClientInfo.setAddress(sender);
            serverToClientInfo.setServerToClientUdpHandler(this);
            serverToClientInfo.getServerToClientChannelFuture().complete(ctx.channel());

        }else {
            userToServerInfo.getUserToServerUdpHandler().channel.writeAndFlush(new DatagramPacket(packet.content().copy(),userToServerInfo.getAddress()));
        }


        super.channelRead(ctx, msg);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SERVER_TO_CLIENT_UDP_HANDLERS_POOL.add(this);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }


}
