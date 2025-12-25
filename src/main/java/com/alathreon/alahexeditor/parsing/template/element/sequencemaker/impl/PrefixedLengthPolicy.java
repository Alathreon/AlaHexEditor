package com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.PreparedLengthPolicy;
import com.alathreon.alahexeditor.util.ByteView;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.*;

public record PrefixedLengthPolicy(int fieldSize) implements LengthPolicy {

    public PrefixedLengthPolicy {
        ensureMaxByteSize(fieldSize, 4);
    }

    @Override
    public PreparedLengthPolicy prepare(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, fieldSize);
        long length = view.parseInt(template.endianness());
        return new PreparedLengthPolicy(data.leftover(view), new CounterLengthPolicyCondition((int)length));
    }
}
