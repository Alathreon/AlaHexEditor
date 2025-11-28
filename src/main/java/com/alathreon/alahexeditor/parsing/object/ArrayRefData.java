package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;

import java.util.List;

public record ArrayRefData(int index, ParseObject data) implements Data {
    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of(new Pair<>("Index", String.valueOf(index)));
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of(new Pair<>("Reference", data));
    }
}
