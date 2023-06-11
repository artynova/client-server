package com.nova.cls.lab2.network.fake;

import com.nova.cls.lab2.packets.BadPacketException;
import com.nova.cls.lab2.packets.Decryptor;
import com.nova.cls.lab2.packets.Packet;
import com.nova.cls.lab2.util.CipherException;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class FakeSender {
    private static boolean verbose = true;
    private final Decryptor decryptor = new Decryptor();

    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        FakeSender.verbose = verbose;
    }

    public void sendPacket(byte[] packet, InetAddress target) {
        try {
            Packet response = decryptor.decrypt(packet);
            if (verbose)
                System.out.println("Time: " + LocalDateTime.now().toLocalTime() + "\nSending " + response + "\nTo " + target + "\n");
        } catch (BadPacketException e) {
            throw new CipherException("Unexpected exception when decrypting a packet encrypted within the program: " + e.getMessage(), e);
        }
    }
}
