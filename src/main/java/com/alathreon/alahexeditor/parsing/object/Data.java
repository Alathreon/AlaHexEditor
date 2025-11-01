package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;
import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.List;

public interface Data {
    @JsonGetter
    default String type() {
        return getClass().getSimpleName();
    }
    List<Pair<String, String>> displayFields();
    List<Pair<String, ParseObject>> children();
}
