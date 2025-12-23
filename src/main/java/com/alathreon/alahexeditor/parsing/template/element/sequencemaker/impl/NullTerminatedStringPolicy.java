package com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.PreparedLengthPolicy;
import com.alathreon.alahexeditor.util.ByteView;
import java.nio.charset.Charset;
import java.util.Objects;

public record NullTerminatedStringPolicy(Charset charset) implements LengthPolicy {

    public NullTerminatedStringPolicy {
        Objects.requireNonNull(charset);
    }

    @Override
    public PreparedLengthPolicy prepare(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        int index = data.findNullChar(charset);
        return new PreparedLengthPolicy(data, new CounterLengthPolicyCondition(index));
    }
}
