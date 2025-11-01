package com.alathreon.alahexeditor.parsing.template.type.union;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.EnumData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaType;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.type.EnumType;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.ensureMaxByteSize;

public record SelfEnumClassifier(int size, LinkedHashMap<Integer, String> bindings) implements Classifier {
    @JsonCreator
    public static SelfEnumClassifier jsonFactory(@JsonProperty("size") int size, @JsonProperty("bindings") List<BindingPair> bindings) {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        for (var constant : bindings) {
            constant.put(map);
        }
        return new SelfEnumClassifier(size, map);
    }
    public SelfEnumClassifier {
        Objects.requireNonNull(bindings, "rangeBindings cannot be null");
        ensureMaxByteSize(size, 4);
    }
    public static ClassifierResult find(String thisName, EnumType enumType, Map<Integer, String> overrideBindings, ByteView data, Template template, ParseObjects objects) throws ParseException {
        SchemaType.ParseTypeResult<EnumData> classifierResult = enumType.parseData(thisName, data, template, objects);
        ByteView leftover = classifierResult.parent().leftover(classifierResult.segment());
        Map<Integer, String> computedBindings = new HashMap<>(enumType.constants());
        computedBindings.putAll(overrideBindings);
        String structName = computedBindings.get(classifierResult.data().code());
        if(structName == null) throw new ParseException(classifierResult.segment(), "No struct binding in union %s".formatted(thisName));
        return new ClassifierResult(structName, classifierResult.data(), classifierResult.data().code(), classifierResult.segment(), leftover);
    }

    @Override
    public ClassifierResult find(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        EnumType enumType = new EnumType(size, bindings);
        return find(thisName, enumType, Map.of(), data, template, objects);
    }
}
