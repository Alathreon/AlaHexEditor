package com.alathreon.alahexeditor.parsing.template.type.union;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.type.EnumType;
import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.util.Pair;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.findType;

public record EnumClassifier(String enumClassifierName, Map<Integer, String> overrideBindings) implements Classifier {
    @JsonCreator
    public static EnumClassifier jsonFactory(@JsonProperty("enumClassifierName") String enumClassifierName, @JsonProperty("overrideBindings") List<BindingPair> overrideBindings) {
        if(overrideBindings == null) {
            overrideBindings = List.of();
        }
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        for (var constant : overrideBindings) {
            constant.put(map);
        }
        return new EnumClassifier(enumClassifierName, map);
    }
    public EnumClassifier {
        Objects.requireNonNull(enumClassifierName, "enumClassifierName cannot be null");
        if (overrideBindings == null) overrideBindings = Map.of();
    }

    @Override
    public ClassifierResult find(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        EnumType enumType = findType(template, data, enumClassifierName, EnumType.class);
        return SelfEnumClassifier.find(enumClassifierName, enumType, overrideBindings, data, template, objects);
    }
}
