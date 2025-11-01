package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.safeSubView;

public record VariableRefSizeArrayElement(String sizeVarName, SchemaElement schema) implements SchemaElement {
    public VariableRefSizeArrayElement {
        Objects.requireNonNull(sizeVarName);
        Objects.requireNonNull(schema);
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ParseObject parseObject = objects.get(sizeVarName);
        if(parseObject == null) throw new ParseException(data, "Expected array size data for variable %s, but got: null".formatted(sizeVarName));
        parseObject = parseObject.resolveReferences();
        if(!(parseObject.data() instanceof IntData(long size, _, _))) throw new ParseException(data, "Expected array size data for variable %s, but got: %s".formatted(sizeVarName, parseObject.data().getClass().getSimpleName()));
        if(size > Integer.MAX_VALUE) throw new ParseException(data, "Array size too large: " + size);
        ByteView view = safeSubView(data, (int) size);
        List<ParseObject> result = new ArrayList<>();
        objects.startScope();
        ArrayData arrayData = new ArrayData(result);
        objects.add(thisName, new ParseObject(view, arrayData));
        for(int i = 0; i < view.parseInt(template.endianness()); i++) {
            ParseStepResult stepResult = schema.parse(thisName, view, template, objects);
            view = stepResult.leftover();
            result.add(stepResult.object());
        }
        objects.endScope();
        return new ParseStepResult(data, view, arrayData);
    }
}
