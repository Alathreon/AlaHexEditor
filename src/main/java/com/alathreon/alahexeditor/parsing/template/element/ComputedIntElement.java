package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.MathParser;
import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.List;
import java.util.Objects;

public record ComputedIntElement(String expression, List<String> variables) implements SchemaElement {

    public ComputedIntElement {
        Objects.requireNonNull(variables);
        if(variables.isEmpty()) throw new IllegalArgumentException("there must be at least one variable");
    }

    private record IntValue(long v, boolean signed) {}

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ParseObject parseObject = objects.get(variables.getFirst());
        IntValue value = find(parseObject, data, 1);
        long longValue = value.v();
        if(expression != null) {
            longValue = MathParser.evalPostfix(expression, value.signed(), objects, parseObject.metadata(), longValue);
        }
        return new ParseStepResult(data, new ParseObject(parseObject.metadata(), new IntData(longValue, value.signed())));
    }
    private IntValue find(ParseObject object, ByteView data, int structIndex) throws ParseException {
        if(object == null) {
            throw new ParseException(data, "Expected int data for variable chain %s but got: null".formatted(String.join("->", variables)));
        }
        return switch (object.data()) {
            case IntData(var raw, var signed, _) -> new IntValue(raw, signed);
            case FloatData(var value) -> new IntValue((long)value, true);
            case BitsetData b -> {
                long[] longs = b.bitSet().toLongArray();
                long l = longs.length > 0 ? longs[longs.length-1] : 0;
                yield new IntValue(l, false);
            }
            case BlobData(var hex) -> new IntValue(Long.parseUnsignedLong(hex.transform(h -> h.length() > 16 ? h.substring(h.length()-16) : h), 16), false);
            case EnumData(var code, _) -> new IntValue(code, false);
            case NullData _ -> new IntValue(0, false);
            case UnionData(_, var intClassifier, _) -> new IntValue(intClassifier, false);
            case StructData(var members) -> {
                if(variables.size() <= structIndex) throw new ParseException(data, "Expected int data for variable chain %s but got nothing".formatted(String.join("->", variables)));
                ParseObject structObject = members.get(variables.get(structIndex));
                yield find(structObject, data, structIndex+1);
            }
            default -> throw new ParseException(data, "Expected data that can be transformed into an int for variable chain %s but got: %s".formatted(String.join("->", variables), object.data().getClass().getSimpleName()));
        };
    }
}

