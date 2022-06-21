package eu.byncing.proxy.protocol.login;

import eu.byncing.proxy.connection.Connection;
import eu.byncing.proxy.nio.buf.NioBuf;
import eu.byncing.proxy.protocol.MinecraftPacket;
import eu.byncing.proxy.protocol.ProtocolSupport;

import java.io.IOException;

public class PacketLoginStart extends MinecraftPacket {

    private String name;

    public PacketLoginStart() {
    }

    public PacketLoginStart(String name) {
        this.name = name;
    }

    @Override
    public void write(Connection connection, NioBuf buf) {
        buf.writeString(name);
    }

    @Override
    public void read(Connection connection, NioBuf buf) throws IOException {
        if (connection.protocol() == ProtocolSupport.MINECRAFT_1_8) {
            name = buf.readString();
        } else connection.close();
    }

    public String name() {
        return name;
    }
}