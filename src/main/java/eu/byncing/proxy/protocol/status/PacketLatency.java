package eu.byncing.proxy.protocol.status;

import eu.byncing.proxy.connection.Connection;
import eu.byncing.proxy.nio.buf.NioBuf;
import eu.byncing.proxy.protocol.MinecraftPacket;

public class PacketLatency extends MinecraftPacket {

    private long payload;

    public PacketLatency() {
    }

    @Override
    public void write(Connection connection, NioBuf buf) {
        buf.writeInt(0x00);
        buf.writeLong(payload);
    }

    @Override
    public void read(Connection connection, NioBuf buf) {
        payload = buf.readLong();
    }

    public long payload() {
        return payload;
    }
}