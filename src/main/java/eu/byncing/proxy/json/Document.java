package eu.byncing.proxy.json;

import java.util.List;
import java.util.Map;

public interface Document<D extends Document<?>> {

    D append(String key, Object value);

    D depend(String key);

    <T> T get(String key, Class<T> clazz);

    <T> List<T> list(String key, Class<T> clazz);

    <T1, T2> Map<T1, T2> map(String key, Class<T1> clazz1, Class<T2> clazz2);
}