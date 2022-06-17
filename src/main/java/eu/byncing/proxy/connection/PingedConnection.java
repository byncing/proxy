package eu.byncing.proxy.connection;

import eu.byncing.proxy.nio.channel.Channel;
import eu.byncing.proxy.protocol.MinecraftPacket;

import java.io.IOException;

public class PingedConnection implements Connection {

    private final Channel channel;

    private State state = State.HANDSHAKE;

    private int protocol;

    public PingedConnection(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        //sending a Minecraft package through the pipeline
    }

    @Override
    public void close() throws IOException {
        channel.close().execute();
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public State state() {
        return state;
    }

    public void state(State state) {
        this.state = state;
    }

    @Override
    public int protocol() {
        return protocol;
    }
}