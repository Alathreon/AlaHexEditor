package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record ComputedStringFormatElement(String format, List<List<String>> variables) implements SchemaElement {

    public ComputedStringFormatElement {
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(variables, "variables");
        variables.forEach(v -> {
            if(v.isEmpty()) throw new IllegalArgumentException("Empty variables");
        });
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        Object[] formatObjects = new Object[variables().size()];
        int i = 0;
        for (List<String> entry : variables()) {
            formatObjects[i++] = find(objects.get(entry.getFirst()), data, entry, 1);
        }
        String formatted = format.formatted(formatObjects);
        return new ParseStepResult(data, new ParseObject(objects.get(variables.getFirst().getFirst()).metadata(), new StringData(formatted)));
    }
    private Object find(ParseObject object, ByteView data, List<String> vars, int structIndex) throws ParseException {
        if(object == null) {
            throw new ParseException(data, "Expected int data for variable chain %s but got: null".formatted(String.join("->", vars)));
        }
        return switch (object.data()) {
            case StringData(var value) -> value;
            case IntData(var raw, _, _) -> raw;
            case BoolData(var value) -> value;
            case FloatData(var value, _) -> value;
            case BitsetData b -> "[" + b.names().stream().collect(Collectors.joining(", ")) + "]";
            case BlobData(var hex) -> hex;
            case EnumData(_, var name, _) -> name;
            case NullData _ -> null;
            case UnionData(_, var intClassifier, _) -> intClassifier;
            case StructData(var members) -> {
                if(vars.size() <= structIndex) throw new ParseException(data, "Expected struct field for variable chain %s but got nothing".formatted(String.join("->", vars)));
                ParseObject structObject = members.get(vars.get(structIndex));
                yield find(structObject, data, vars, structIndex+1);
            }
            default -> throw new ParseException(data, "Unexpected data type for variable chain %s but got %s".formatted(String.join("->", vars), object.data().getClass().getSimpleName()));
        };
    }
}

