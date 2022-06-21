package eu.byncing.proxy.nio.channel;

import eu.byncing.proxy.ProxyServer;
import eu.byncing.proxy.callback.Callback;
import eu.byncing.proxy.callback.DefaultCallback;
import eu.byncing.proxy.nio.Address;
import eu.byncing.proxy.nio.buf.NioBuf;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class NioChannel implements Channel {

    private final NioBuf buf = new NioBuf(0, false);

    private final SocketChannel socket;
    private boolean connected;

    private final Future future;

    private Address address;

    public NioChannel(SocketChannel socket) throws IOException {
        this.socket = socket;
        this.address = Address.build(socket);
        this.future = new Future(this);
        this.connected = true;
    }

    public NioChannel() throws IOException {
        this.socket = SocketChannel.open();
        this.future = new Future(this);
    }

    @Override
    public void connect(SocketAddress address) throws IOException {
        this.socket.connect(address);
        this.address = Address.build(socket);
    }

    @Override
    public NioBuf read() {
        while (true) {
            try {
                if (!connected) return null;
                int read = socket.read(ProxyServer.BUFFER);
                if (read == -1) return null;
                if (read == 0) break;

                buf.enlarge(read);
                buf.writeBuf(ProxyServer.BUFFER);

                ProxyServer.BUFFER.clear();
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
        if (buf.offset() == 0) return new NioBuf(0, false);
        buf.reset();
        return buf;
    }

    @Override
    public Callback<Channel> write(NioBuf buf) {
        DefaultCallback<Channel> callback = new DefaultCallback<>();
        callback.run(() -> {
            try {
                NioBuf buffer = new NioBuf(0, false);

                buf.reset();

                int readable = buf.readable();
                buffer.writeInt(readable);
                buffer.writeBuf(buf);

                buffer.reset();
                socket.write(buffer.toNio());

                callback.complete(future.channel);
            } catch (IOException e) {
                callback.failure(e);
            }
        });

        return callback;
    }

    @Override
    public Callback<Channel> close() {
        DefaultCallback<Channel> callback = new DefaultCallback<>();
        callback.run(() -> connected = false);
        return callback;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public Channel.Future future() {
        return future;
    }

    @Override
    public SocketAddress localAddress() {
        return address.localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return address.remoteAddress();
    }

    public static class Future implements Channel.Future {

        private final NioChannel channel;

        public Future(NioChannel channel) {
            this.channel = channel;
        }

        @Override
        public Callback<Channel.Future> close() {
            DefaultCallback<Channel.Future> callback = new DefaultCallback<>();
            callback.run(() -> {
                try {
                    channel.socket.close();
                    callback.complete(channel.future);
                } catch (IOException e) {
                    callback.failure(e);
                    throw new RuntimeException(e);
                }
            });
            return callback;
        }

        @Override
        public SocketAddress localAddress() {
            return channel.localAddress();
        }

        @Override
        public SocketAddress remoteAddress() {
            return channel.remoteAddress();
        }
    }
}