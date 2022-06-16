package eu.byncing.proxy.callback;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultCallback<E> implements Callback<E> {

    private final Collection<Listener<E>> listeners = new ConcurrentLinkedQueue<>();

    private Runnable runnable;

    @Override
    public final Callback<E> addListener(Listener<E> listener) {
        listeners.add(listener);
        return this;
    }

    public void run(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void complete(E element) {
        for (Listener<E> listener : listeners) {
            listener.complete(this, element);
            listeners.remove(listener);
        }
    }

    @Override
    public void failure(Throwable throwable) {
        for (Listener<E> listener : listeners) {
            listener.failure(this, throwable);
            listeners.remove(listener);
        }
    }

    @Override
    public void execute() {
        if (runnable != null) runnable.run();
    }
}