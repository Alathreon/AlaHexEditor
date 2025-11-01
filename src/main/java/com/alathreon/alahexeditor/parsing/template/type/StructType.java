package com.alathreon.alahexeditor.parsing.template.type;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.object.StructData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.SchemaType;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.LinkedHashMap;
import java.util.Map;

public record StructType(LinkedHashMap<String, SchemaElement> members) implements SchemaType<StructData> {
    @Override
    public ParseTypeResult<StructData> parseData(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = data;
        LinkedHashMap<String, ParseObject> result = new LinkedHashMap<>();
        objects.startScope();
        StructData structData = new StructData(result);
        objects.add(thisName, new ParseObject(view, structData));
        for (Map.Entry<String, SchemaElement> entry : members().entrySet()) {
            ParseStepResult stepResult = entry.getValue().parse(entry.getKey(), view, template, objects);
            view = stepResult.leftover();
            result.put(entry.getKey(), stepResult.object());
        }
        objects.endScope();
        return new ParseTypeResult<>(data, data.subView(0, view.offset() - data.offset()), structData);
    }
}
