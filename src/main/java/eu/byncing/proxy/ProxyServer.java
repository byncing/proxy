package eu.byncing.proxy;

import eu.byncing.proxy.connection.Connection;
import eu.byncing.proxy.connection.PingedConnection;
import eu.byncing.proxy.nio.KeyAction;
import eu.byncing.proxy.nio.buf.NioBuf;
import eu.byncing.proxy.nio.channel.Channel;
import eu.byncing.proxy.nio.channel.NioChannel;
import eu.byncing.proxy.protocol.handshake.PacketHandshake;
import eu.byncing.proxy.protocol.login.PacketLoginStart;
import eu.byncing.proxy.protocol.status.PacketLatency;
import eu.byncing.proxy.protocol.status.PacketRequest;
import eu.byncing.proxy.protocol.status.PacketResponse;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class ProxyServer implements Closeable {


    public static final ByteBuffer BUFFER = ByteBuffer.allocate(128);
    public static final PingList PING_LIST = new PingList(new PingList.Version("Unstable", 0x00), new PingList.Players(22, 0), "Something different but nothing special");

    private boolean connected;

    private final KeyAction action;
    private final ServerSocketChannel socket;

    private final Map<SocketAddress, Channel> channels = new HashMap<>();
    private final Map<Channel, Connection> connections = new HashMap<>();

    public ProxyServer() throws IOException {
        this.action = new KeyAction();
        this.socket = ServerSocketChannel.open();
    }

    public static void main(String[] args) {
        try {
            ProxyServer server = new ProxyServer();
            server.bind(new InetSocketAddress(25565), 128);
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
                    Connection connection = new PingedConnection(channel);

                    channels.put(socket.getRemoteAddress(), channel);
                    connections.put(channel, connection);
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

                        Connection connection = connections.get(channel);
                        connections.remove(channel, connection);

                        return;
                    }

                    while (buf.isReadable()) {
                        int length = buf.readInt();
                        if (length < 0 || length > buf.capacity()) break;

                        byte[] bytes = new byte[length];
                        buf.readBytes(bytes, 0, length);

                        NioBuf packet = new NioBuf(bytes);
                        int id = packet.readInt();

                        PingedConnection connection = (PingedConnection) connections.get(channel);
                        int protocol = connection.protocol();

                        if (connection.state() == Connection.State.HANDSHAKE) {
                            if (id == 0x00) {
                                PacketHandshake handshake = new PacketHandshake();
                                handshake.read(connection, packet);

                                connection.protocol(handshake.protocol());

                                if (handshake.state() == 1) {
                                    connection.state(Connection.State.STATUS);
                                } else connection.state(Connection.State.LOGIN);
                            }
                            continue;
                        }

                        if (connection.state() == Connection.State.STATUS) {
                            if (id == 0x00) {
                                PacketRequest request = new PacketRequest();

                                NioBuf response = new NioBuf(0, false);
                                PacketResponse packetResponse = new PacketResponse(PING_LIST);

                                packetResponse.write(connection, response);
                                channel.write(response).execute();
                            }
                            if (id == 0x01) {
                                PacketLatency latency = new PacketLatency();
                                latency.read(connection, packet);

                                NioBuf response = new NioBuf(0, false);
                                response.writeInt(0x01);
                                response.writeLong(latency.payload());

                                channel.write(response).execute();

                                System.out.println(channel.remoteAddress() + " has pinged.");
                            }
                        }

                        if (connection.state() == Connection.State.LOGIN) {
                            if (id == 0x00) {
                                PacketLoginStart login = new PacketLoginStart();
                                login.read(connection, packet);

                                System.out.println(login.name() + " has connected.");
                            }
                        }
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