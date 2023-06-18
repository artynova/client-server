package com.nova.cls.network.udp;

import java.net.InetAddress;

public class Constants {
    public static final InetAddress SERVER_ADDRESS = InetAddress.getLoopbackAddress();
    public static final int SERVER_PORT = 8081;
    public static final int MAX_PACKET_SIZE = 1492; // based on standard maximum transmission unit of 1500 bytes, which, subtracting 8 bytes of the UDP header, leaves 1492 body bytes
}
