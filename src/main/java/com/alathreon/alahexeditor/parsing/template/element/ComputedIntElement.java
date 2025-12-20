package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.MathParser;
import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

public record ComputedIntElement(String expression, List<String> variables) implements SchemaElement {

    public ComputedIntElement {
        Objects.requireNonNull(variables);
        if(variables.isEmpty()) throw new IllegalArgumentException("there must be at least one variable");
    }

    private record IntValue(long v, boolean signed) {}

    private int bitsToSize(int length) {
        if(length <= 8) return 1;
        if(length <= 16) return 2;
        if(length <= 32) return 4;
        return 8;
    }
    private int bytesToSize(int length) {
        if(length <= 1) return 1;
        if(length == 2) return 2;
        if(length <= 4) return 4;
        return 8;
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ParseObject parseObject = objects.get(variables.getFirst());
        IntData value = find(parseObject, data, 1);
        if(expression != null) {
            value = MathParser.evalPostfix(expression, objects, parseObject.metadata(), value);
        }
        return new ParseStepResult(data, new ParseObject(parseObject.metadata(), value));
    }
    private IntData find(ParseObject object, ByteView data, int structIndex) throws ParseException {
        if(object == null) {
            throw new ParseException(data, "Expected int data for variable chain %s but got: null".formatted(String.join("->", variables)));
        }
        return switch (object.data()) {
            case IntData d -> d;
            case FloatData(var value, var size) -> new IntData((long) value, true, size);
            case BoolData(var value) -> new IntData(value ? BigInteger.ONE : BigInteger.ZERO, false, 1);
            case BitsetData b -> {
                long l = Long.parseUnsignedLong(b.bits(), 2);
                yield new IntData(l, false, bitsToSize(b.bits().length()));
            }
            case BlobData(var hex) -> {
                int size = bytesToSize(hex.length()/2);
                yield new IntData(IntData.constraints(new BigInteger(hex.substring(0, size*2), 16), false, size), false, size);
            }
            case EnumData(var code, var size, _) -> new IntData(code, false, size);
            case NullData _ -> new IntData(0, false, 1);
            case UnionData(_, var intClassifier, _) -> intClassifier;
            case StructData(var members) -> {
                if(variables.size() <= structIndex) throw new ParseException(data, "Expected int data for variable chain %s but got nothing".formatted(String.join("->", variables)));
                ParseObject structObject = members.get(variables.get(structIndex));
                yield find(structObject, data, structIndex+1);
            }
            default -> throw new ParseException(data, "Expected data that can be transformed into an int for variable chain %s but got: %s".formatted(String.join("->", variables), object.data().getClass().getSimpleName()));
        };
    }
}

