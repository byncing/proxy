package eu.byncing.proxy.nio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.function.Consumer;

public class KeyAction implements Closeable {

    private final Selector selector;

    private boolean open;

    public KeyAction() throws IOException {
        this.selector = Selector.open();
        this.open = true;
    }

    public void event(Consumer<SelectionKey> consumer, long blocking) {
        while (open) {
            try {
                selector.select();

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isValid()) consumer.accept(key);

                    Thread.sleep(blocking);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() {
        open = false;
    }

    public Selector selector() {
        return selector;
    }

    public boolean isOpen() {
        return open;
    }
}