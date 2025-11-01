package com.alathreon.alahexeditor.util;

import java.util.Map;

public record Pair<K, V>(K key, V value) {
    public void put(Map<K, V> map) {
        map.put(key, value);
    }
}
