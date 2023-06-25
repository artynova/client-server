package com.nova.cls.network.udp;

public class UDPInfo {
    private long nextPacketId; // is only checked if
    private long sessionExpireTime; // in millis
    private int retransmissionRequests = 0;
    // when this number hits a threshold, the server stops trying to acquire an old packet from the client

    public UDPInfo(long nextPacketId, long sessionExpireTime) {
        this.nextPacketId = nextPacketId;
        this.sessionExpireTime = sessionExpireTime;
    }

    public long getNextPacketId() {
        return nextPacketId;
    }

    public void setNextPacketId(long nextPacketId) {
        this.nextPacketId = nextPacketId;
    }

    public long getSessionExpireTime() {
        return sessionExpireTime;
    }

    public void setSessionExpireTime(long sessionExpireTime) {
        this.sessionExpireTime = sessionExpireTime;
    }

    public int getRetransmissionRequests() {
        return retransmissionRequests;
    }

    public void setRetransmissionRequests(int retransmissionRequests) {
        this.retransmissionRequests = retransmissionRequests;
    }
}
