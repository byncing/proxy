package eu.byncing.proxy.protocol.status;

import eu.byncing.proxy.PingList;
import eu.byncing.proxy.connection.Connection;
import eu.byncing.proxy.nio.buf.NioBuf;
import eu.byncing.proxy.protocol.MinecraftPacket;

public class PacketResponse extends MinecraftPacket {

    private PingList pingList;

    public PacketResponse() {
    }

    public PacketResponse(PingList pingList) {
        this.pingList = pingList;
    }

    @Override
    public void write(Connection connection, NioBuf buf) {
        buf.writeInt(0x00);
        buf.writeString(pingList.toJson());
    }

    @Override
    public void read(Connection connection, NioBuf buf) {
    }
}