package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;

import java.util.List;
import java.util.stream.IntStream;

public record ArrayData(List<ParseObject> data) implements Data {
    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of();
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return IntStream.range(0, data.size()).mapToObj(i -> new Pair<>(String.valueOf(i), data.get(i))).toList();
    }
}
