package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.ArrayData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.ensureMaxByteSize;

public record FixedSizeArrayElement(int size, SchemaElement schema) implements SchemaElement {
    public FixedSizeArrayElement {
        Objects.requireNonNull(schema);
        ensureMaxByteSize(size, 4);
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = data;
        List<ParseObject> result = new ArrayList<>();
        objects.startScope();
        ArrayData arrayData = new ArrayData(result);
        objects.add(thisName, new ParseObject(view, arrayData));
        for(int i = 0; i < size; i++) {
            ParseStepResult stepResult = schema.parse(thisName, view, template, objects);
            view = stepResult.leftover();
            result.add(stepResult.object());
        }
        objects.endScope();
        return new ParseStepResult(data, view, arrayData);
    }
}
