package com.alathreon.alahexeditor.parsing.template.type.union;

import com.alathreon.alahexeditor.parsing.deserializer.IntegerDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

public record BindingPair(
        @JsonDeserialize(using = IntegerDeserializer.class) int key,
        String value) {
    public void put(Map<Integer, String> map) {
        map.put(key, value);
    }
}
