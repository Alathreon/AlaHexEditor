package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;

import java.util.BitSet;
import java.util.List;

public record BitsetData(BitSet bitSet, List<String> names) implements Data {
    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of(
                new Pair<>("Bits", bitSet.toString()),
                new Pair<>("Names", String.join(", ", names))
        );
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of();
    }
}
