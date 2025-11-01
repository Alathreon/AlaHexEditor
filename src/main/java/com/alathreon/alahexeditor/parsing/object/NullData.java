package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;

import java.util.List;

public record NullData() implements Data {
    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of();
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of();
    }
}
