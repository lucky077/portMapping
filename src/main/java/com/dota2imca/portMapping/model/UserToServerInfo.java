package com.dota2imca.portMapping.model;

import com.dota2imca.portMapping.handler.UserToServerUdpHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;

@Data
@Accessors(chain = true)
public class UserToServerInfo {

    private InetSocketAddress address;

    private UserToServerUdpHandler userToServerUdpHandler;

}
