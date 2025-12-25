package com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.deserializer.IntegerDeserializer;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.PreparedLengthPolicy;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.ensureNonNegative;

public record FixedLengthPolicy(@JsonDeserialize(using = IntegerDeserializer.class) int length) implements LengthPolicy {

    public FixedLengthPolicy {
        ensureNonNegative(length);
    }

    @Override
    public PreparedLengthPolicy prepare(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        return new PreparedLengthPolicy(data, new CounterLengthPolicyCondition(length));
    }
}
