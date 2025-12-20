package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;

import java.util.LinkedHashMap;
import java.util.List;

public record StructData(LinkedHashMap<String, ParseObject> members) implements Data {
    public static StructData from(List<Pair<String, ParseObject>> members) {
        LinkedHashMap<String, ParseObject> linkedHashMap = new LinkedHashMap<>();
        for(Pair<String, ParseObject> entry : members) {
            linkedHashMap.put(entry.key(), entry.value());
        }
        return new StructData(linkedHashMap);
    }
    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of();
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return members.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).toList();
    }
}
