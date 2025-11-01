package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record IntData(@JsonIgnore long raw, @JsonIgnore boolean signed, String data) implements Data {
    public IntData(long raw, boolean signed) {
        this(raw, signed, signed ? Long.toString(raw) : Long.toUnsignedString(raw));
    }

    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of(new Pair<>("Value", data));
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of();
    }
}
