package com.alathreon.alahexeditor.parsing.template.type;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.EnumData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaType;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.TemplateUtil;
import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.util.Pair;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.List;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.safeSubView;

public record EnumType(int size, LinkedHashMap<Integer, String> constants) implements SchemaType<EnumData> {
    @JsonCreator
    public static EnumType jsonFactory(@JsonProperty("size") int size, @JsonProperty("constants") List<Pair<Integer, String>> constants) {
        LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
        for (Pair<Integer, String> constant : constants) {
            constant.put(map);
        }
        return new EnumType(size, map);
    }
    public EnumType {
        TemplateUtil.ensureMaxByteSize(size, 4);
    }

    @Override
    public ParseTypeResult<EnumData> parseData(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, size());
        int code = (int) view.parseInt(template.endianness());
        String constantName = constants().get(code);
        if(constantName == null) throw new ParseException(view, "Error in Enum %s: constant %s not found".formatted(thisName, view));
        return new ParseTypeResult<>(data, view, new EnumData(code, size, constantName));
    }
}
