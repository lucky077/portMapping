package com.dota2imca.portMapping.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class Util {

    public static int getFreePort(int start,String protocol){

        for (int i = start ; i < 60000 ; i++) {
            try {
                if ("tcp".equals(protocol)){
                    new ServerSocket(i).close();
                }else if ("udp".equals(protocol)){
                    new DatagramSocket(i).close();
                }

                return i;
            } catch (IOException e) {
            }
        }
        return -1;
    }


}
