package com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl;

import java.math.BigInteger;
import java.util.Objects;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.PreparedLengthPolicy;
import com.alathreon.alahexeditor.util.ByteView;

public record ReferencedLengthPolicy(String sizeVarName) implements LengthPolicy {

    public ReferencedLengthPolicy {
        Objects.requireNonNull(sizeVarName);
    }

    @Override
    public PreparedLengthPolicy prepare(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ParseObject parseObject = objects.get(sizeVarName);
        if(parseObject == null) throw new ParseException(data, "Expected array size data for variable %s, but got: null".formatted(sizeVarName));
        parseObject = parseObject.resolveReferences();
        if(!(parseObject.data() instanceof IntData(var size, _, _))) throw new ParseException(data, "Expected array size data for variable %s, but got: %s".formatted(sizeVarName, parseObject.data().getClass().getSimpleName()));
        if(size.compareTo(BigInteger.ZERO) < 0) throw new ParseException(data, "Array size negative: " + size);
        if(size.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) throw new ParseException(data, "Array size too large: " + size);
        int intSize = size.intValue();
        return new PreparedLengthPolicy(data, new CounterLengthPolicyCondition(intSize));
    }
}
