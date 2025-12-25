package com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicyCondition;
import com.alathreon.alahexeditor.util.ByteView;

public class CounterLengthPolicyCondition implements LengthPolicyCondition {
    private final int limitExclusive;
    private int i;

    public CounterLengthPolicyCondition(int limitExclusive) {
        this.limitExclusive = limitExclusive;
    }

    @Override
    public boolean hasNext(ParseObject self, String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        return i++ < limitExclusive;
    }

}
