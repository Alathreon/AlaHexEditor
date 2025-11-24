package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.BoolData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.safeSubView;

public record BoolElement() implements SchemaElement {

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, 1);
        long value = view.parseInt(template.endianness());
        return new ParseStepResult(data, view, new BoolData(value == 0));
    }
}
