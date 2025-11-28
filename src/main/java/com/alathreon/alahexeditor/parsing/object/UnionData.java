package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;

import java.util.List;

public record UnionData(ParseObject classifier, IntData intClassifier, ParseObject data) implements Data {
    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of(new Pair<>("ClassifierValue", intClassifier.toString()));
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of(
                new Pair<>("Classifier", classifier),
                new Pair<>("Data", data)
        );
    }
}
