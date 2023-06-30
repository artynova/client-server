package com.nova.cls.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Constants {
    public static final InetSocketAddress SERVER_EXTERNAL_ADDRESS =
        new InetSocketAddress(InetAddress.getLoopbackAddress(), 8080);
}
