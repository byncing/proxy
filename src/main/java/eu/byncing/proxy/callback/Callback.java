package eu.byncing.proxy.callback;

public interface Callback<E> {

    Callback<E> addListener(Listener<E> listener);

    void execute();

    void complete(E element);

    void failure(Throwable throwable);

    interface Listener<E> {

        void complete(Callback<E> callback, E element);

        void failure(Callback<E> callback, Throwable throwable);
    }
}