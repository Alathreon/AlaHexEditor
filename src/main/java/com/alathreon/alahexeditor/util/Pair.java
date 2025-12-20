package com.alathreon.alahexeditor.util;

import java.util.Map;

public record Pair<K, V>(K key, V value) {

    public static <K, V> Pair<K, V> of(K k, V v) {
        return new Pair<>(k, v);
    }

    public void put(Map<K, V> map) {
        map.put(key, value);
    }
}
