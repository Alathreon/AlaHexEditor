package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;

import java.util.List;

public record BlobData(String hex) implements Data {
    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of(new Pair<>("Hex", hex));
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of();
    }
}
