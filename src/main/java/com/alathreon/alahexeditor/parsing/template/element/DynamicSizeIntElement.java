package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.math.BigInteger;
import java.util.Objects;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.*;

public record DynamicSizeIntElement(String encoding) implements SchemaElement {

    public DynamicSizeIntElement {
        Objects.requireNonNull(encoding);
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        return switch (encoding) {
            case "ULEB128" -> parseULEB128(data, objects);
            default -> throw new IllegalArgumentException("Invalid encoding: \"" + encoding + "\"");
        };
    }

    private ParseStepResult parseULEB128(ByteView data, ParseObjects objects) throws ParseException {
        BigInteger result = BigInteger.ZERO;
        int shift = 0;

        int i = 0;
        while(true) {
            if(i >= data.length()) {
                throw new ParseException(data, objects, "index " + i + " invalid for length " + data.length());
            }
            int b = data.get(i) & 0xFF;
            BigInteger chunk = BigInteger.valueOf(b & 0x7F).shiftLeft(shift);
            result = result.or(chunk);
            if ((b & 0x80) == 0) {
                break;
            }
            shift += 7;
            i++;
        }

        int bitLength = result.bitLength();
        int byteCount = (bitLength + 7) / 8;
        return new ParseStepResult(data, data.subView(i+1), new IntData(result, false, byteCount));
    }
}
