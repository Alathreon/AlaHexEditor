package com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.deserializer.IntegerDeserializer;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.PreparedLengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl.PredicateTerminatedPolicyCondition.Operator;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

public record PredicateTerminatedPolicy(
        String structField,
        @JsonDeserialize(using = IntegerDeserializer.class) long against,
        Operator operator) implements LengthPolicy {

    public PredicateTerminatedPolicy {
        Objects.requireNonNull(operator,  "operator");
    }

    @Override
    public PreparedLengthPolicy prepare(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        return new PreparedLengthPolicy(data, new PredicateTerminatedPolicyCondition(structField, against, operator));
    }
}
