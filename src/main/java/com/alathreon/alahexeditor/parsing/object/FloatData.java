package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;

import java.util.List;

public record FloatData(double data, int size) implements Data {
    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of(
                new Pair<>("Value", String.valueOf(data)),
                new Pair<>("Size", String.valueOf(size))
        );
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of();
    }
}
