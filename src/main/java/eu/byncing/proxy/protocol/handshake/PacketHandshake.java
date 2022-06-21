package eu.byncing.proxy.protocol.handshake;

import eu.byncing.proxy.connection.Connection;
import eu.byncing.proxy.nio.buf.NioBuf;
import eu.byncing.proxy.protocol.MinecraftPacket;

public class PacketHandshake extends MinecraftPacket {

    private int protocol;
    private String host;
    private int port, state;

    public PacketHandshake() {
    }

    public PacketHandshake(int protocol, String host, short port, int state) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.state = state;
    }

    @Override
    public void write(Connection connection, NioBuf buf) {
        buf.writeInt(protocol);
        buf.writeString(host);
        buf.writeShort((short) port);
        buf.writeInt(state);
    }

    @Override
    public void read(Connection connection, NioBuf buf) {
        protocol = buf.readInt();
        host = buf.readString();
        port = buf.readShort();
        state = buf.readInt();
    }

    public int protocol() {
        return protocol;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public int state() {
        return state;
    }

    @Override
    public String toString() {
        return "PacketHandshake{" +
                "protocol=" + protocol +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", state=" + state +
                '}';
    }
}