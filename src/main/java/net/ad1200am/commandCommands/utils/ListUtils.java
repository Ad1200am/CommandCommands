package net.ad1200am.commandCommands.utils;

import java.util.*;

public class ListUtils {

    public static <T> List<T> of(T ... entries) {
        return Arrays.asList(entries);
    }

    public static <K, V> boolean isInstanceOf(Map<?, ?> map, Class<K> kClass, Class<V> vClass) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!kClass.isInstance(entry.getKey()) || vClass.isInstance(entry.getValue())) return false;
        }
        return true;
    }

    public static <K, V> Map<K, V> forceCastMap(Map<?, ?> map, Class<K> kClass, Class<V> vClass) {
        Map<K, V> output = new HashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!kClass.isInstance(entry.getKey()) || vClass.isInstance(entry.getValue())) continue;
            output.put(kClass.cast(entry.getKey()), vClass.cast(entry.getValue()));
        }
        return output;
    }
}
