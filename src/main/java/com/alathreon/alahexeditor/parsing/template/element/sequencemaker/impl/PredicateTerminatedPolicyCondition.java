package com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.BoolData;
import com.alathreon.alahexeditor.parsing.object.Data;
import com.alathreon.alahexeditor.parsing.object.EnumData;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.object.StructData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicyCondition;
import com.alathreon.alahexeditor.util.ByteView;

public class PredicateTerminatedPolicyCondition implements LengthPolicyCondition {

    enum Operator {
        EQUALS, NOT_EQUALS,
        LESS_THAN, LESS_THAN_OR_EQUALS,
        MORE_THAN, MORE_THAN_OR_EQUALS,
    }

    private final String structField;
    private final long against;
    private final Operator operator;

    public PredicateTerminatedPolicyCondition(String structField, long against, Operator operator) {
        this.structField = structField;
        this.against = against;
        this.operator = operator;
    }

    @Override
    public boolean hasNext(ParseObject self, String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        if(self == null) return true;
        return test(data, self.data());
    }

    private boolean test(ByteView data, Data object) throws ParseException {
        if(structField != null) {
            if(!(object instanceof StructData(var members))) throw new ParseException(data, "Expected struct data with field %s, but got: %s".formatted(structField, data.getClass().getSimpleName()));
            ParseObject parseObject = members.get(structField);
            if(parseObject == null) throw new ParseException(data, "Field %s not found in struct data".formatted(structField));
            object = parseObject.data();
        }
        long value = switch (object) {
            case IntData(var raw, _, _) -> raw.longValue();
            case BoolData(boolean v) -> v ? 1 : 0;
            case EnumData(int code, _, _) -> code;
            case UnionData(_, var intClassifier, _) -> intClassifier.value().longValue();
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
