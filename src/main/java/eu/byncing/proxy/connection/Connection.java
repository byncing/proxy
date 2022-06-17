package eu.byncing.proxy.connection;

import eu.byncing.proxy.nio.channel.Channel;
import eu.byncing.proxy.protocol.MinecraftPacket;
import eu.byncing.proxy.protocol.PacketSender;

import java.io.Closeable;
import java.io.IOException;

public interface Connection extends PacketSender, Closeable {

    enum State {
        PENDING, HANDSHAKE, STATUS, LOGIN, PLAY
    }

    @Override
    void sendPacket(MinecraftPacket packet);

    @Override
    void close() throws IOException;

    Channel channel();

    State state();

    int protocol();
}