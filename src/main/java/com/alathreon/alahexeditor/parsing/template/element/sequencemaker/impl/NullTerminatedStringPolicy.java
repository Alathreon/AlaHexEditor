package com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.PreparedLengthPolicy;
import com.alathreon.alahexeditor.util.ByteView;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class NullTerminatedStringPolicy implements LengthPolicy {

    private Charset charset = StandardCharsets.UTF_8;

    public void setCharset(Charset charset) {
        this.charset = Objects.requireNonNull(charset);
    }

    @Override
    public PreparedLengthPolicy prepare(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        int index = data.findEndOfString(charset);
        if(index < data.length()) {
            index++;
        }
        return new PreparedLengthPolicy(data, new CounterLengthPolicyCondition(index));
    }
}
