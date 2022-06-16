package eu.byncing.proxy;

import eu.byncing.proxy.nio.KeyAction;
import eu.byncing.proxy.nio.buf.NioBuf;
import eu.byncing.proxy.nio.channel.Channel;
import eu.byncing.proxy.nio.channel.NioChannel;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProxyServer implements Closeable {

    public static final ByteBuffer BUFFER = ByteBuffer.allocate(128);

    private boolean connected;

    private final KeyAction action;
    private final ServerSocketChannel socket;

    private final Map<SocketAddress, Channel> channels = new HashMap<>();

    public ProxyServer() throws IOException {
        this.action = new KeyAction();
        this.socket = ServerSocketChannel.open();
    }

    public static void main(String[] args) {
        try {
            ProxyServer server = new ProxyServer();
            server.bind(new InetSocketAddress(25565), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void bind(SocketAddress address, int backlog) throws IOException {
        if (connected) return;
        socket.bind(address, backlog);
        socket.configureBlocking(false).register(action.selector(), SelectionKey.OP_ACCEPT);

        connected = true;

        action.event(key -> {
            if (key.isAcceptable()) {
                try {
                    SocketChannel socket = this.socket.accept();
                    socket.configureBlocking(false).register(action.selector(), SelectionKey.OP_READ);

                    Channel channel = new NioChannel(socket);

                    channels.put(socket.getRemoteAddress(), channel);

                    System.out.println(channel.remoteAddress() + " has connected.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (key.isReadable()) {
                try {
                    SocketChannel socket = (SocketChannel) key.channel();
                    Channel channel = channels.get(socket.getRemoteAddress());
                    if (channel == null) return;

                    NioBuf buf = channel.read();
                    if (buf == null) {
                        channels.remove(channel.remoteAddress(), channel);
                        channel.future().close().execute();

                        System.out.println(channel.remoteAddress() + " has disconnected.");
                        return;
                    }

                    while (buf.isReadable()) {
                        int length = buf.readInt();
                        if (length < 0 || length > buf.capacity()) break;

                        byte[] bytes = new byte[length];
                        buf.readBytes(bytes, 0, length);

                        System.out.println("data array: " + Arrays.toString(bytes));
                    }
                    buf.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    public boolean isConnected() {
        return connected;
    }
}