package eu.byncing.proxy.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public interface Address {

    static Address build(SocketAddress localAddress, SocketAddress remoteAddress) {
        return new Address() {

            final SocketAddress localAddress0, remoteAddress0;

            {
                localAddress0 = localAddress;
                remoteAddress0 = remoteAddress;
            }

            @Override
            public SocketAddress localAddress() {
                return localAddress0;
            }

            @Override
            public SocketAddress remoteAddress() {
                return remoteAddress0;
            }
        };
    }

    static Address build(SocketChannel channel) throws IOException {
        return build(channel.getLocalAddress(), channel.getRemoteAddress());
    }

    SocketAddress localAddress();

    SocketAddress remoteAddress();
}