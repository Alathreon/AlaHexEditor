package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;

import java.util.List;

public record StringData(String data) implements Data {
    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of(new Pair<>("Value", data));
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of();
    }
}
