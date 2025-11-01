package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.deserializer.IntegerDeserializer;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record DynamicSizeArrayElement(
        String structField,
        @JsonDeserialize(using = IntegerDeserializer.class) long against,
        Operator operator,
        SchemaElement schema) implements SchemaElement {
    public DynamicSizeArrayElement {
        Objects.requireNonNull(operator,  "operator");
        Objects.requireNonNull(schema, "schema");
    }

    enum Operator {
        EQUALS, NOT_EQUALS,
        LESS_THAN, LESS_THAN_OR_EQUALS,
        MORE_THAN, MORE_THAN_OR_EQUALS,
    }
    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = data;
        List<ParseObject> result = new ArrayList<>();
        objects.startScope();
        ArrayData arrayData = new ArrayData(result);
        objects.add(thisName, new ParseObject(view, arrayData));
        do {
            ParseStepResult stepResult = schema.parse(thisName, view, template, objects);
            view = stepResult.leftover();
            result.add(stepResult.object());
        } while (test(data, result.getLast().data()));
        objects.endScope();
        return new ParseStepResult(data, data.subView(0, view.offset()), arrayData);
    }
    private boolean test(ByteView data, Data object) throws ParseException {
        if(structField != null) {
            if(!(object instanceof StructData(var members))) throw new ParseException(data, "Expected struct data with field %s, but got: %s".formatted(structField, data.getClass().getSimpleName()));
            ParseObject parseObject = members.get(structField);
            if(parseObject == null) throw new ParseException(data, "Field %s not found in struct data".formatted(structField));
            object = parseObject.data();
        }
        long value = switch (object) {
            case IntData(long raw, _, _) -> raw;
            case EnumData(int code, _) -> code;
            case UnionData(_, int intClassifier, _) -> intClassifier;
            default -> throw new ParseException(data, "Expected int data, enum data, or union data, but got: %s".formatted(object.getClass().getSimpleName()));
        };
        return switch (operator) {
            case EQUALS -> value == against;
            case NOT_EQUALS -> value != against;
            case LESS_THAN -> value < against;
            case LESS_THAN_OR_EQUALS -> value <= against;
            case MORE_THAN -> value > against;
            case MORE_THAN_OR_EQUALS -> value >= against;
        };
    }
}
