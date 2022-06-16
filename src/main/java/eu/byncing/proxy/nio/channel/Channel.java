package eu.byncing.proxy.nio.channel;

import eu.byncing.proxy.callback.Callback;
import eu.byncing.proxy.nio.Address;
import eu.byncing.proxy.nio.buf.NioBuf;

import java.io.IOException;
import java.net.SocketAddress;

public interface Channel extends Address {

    void connect(SocketAddress address) throws IOException;

    NioBuf read();

    Callback<Channel> write(NioBuf buf);

    Callback<Channel> close() throws IOException;

    boolean isConnected();

    Future future();

    @Override
    SocketAddress localAddress();

    @Override
    SocketAddress remoteAddress();

    interface Future extends Address {

        Callback<Future> close();

        @Override
        SocketAddress localAddress();

        @Override
        SocketAddress remoteAddress();
    }
}