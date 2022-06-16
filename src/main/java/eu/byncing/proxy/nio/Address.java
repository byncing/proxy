package eu.byncing.proxy.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public interface Address {

    static Address build(SocketChannel channel) {
        return new Address() {

            final SocketAddress localAddress, remoteAddress;

            {
                try {
                    localAddress = channel.getLocalAddress();
                    remoteAddress = channel.getRemoteAddress();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public SocketAddress localAddress() {
                return localAddress;
            }

            @Override
            public SocketAddress remoteAddress() {
                return remoteAddress;
            }
        };
    }

    SocketAddress localAddress();

    SocketAddress remoteAddress();
}