package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.parsing.deserializer.IntegerDeserializer;
import com.alathreon.alahexeditor.util.Pair;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public record IntRangeBindingData(
        String name,
        @JsonDeserialize(using = IntegerDeserializer.class) int from,
        @JsonDeserialize(using = IntegerDeserializer.class) int to) implements Data {
    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of(
                new Pair<>("Name", name),
                new Pair<>("From", String.valueOf(from)),
                new Pair<>("To", String.valueOf(to))
        );
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of();
    }
}
