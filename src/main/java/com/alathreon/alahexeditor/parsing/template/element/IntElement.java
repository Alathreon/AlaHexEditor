package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.MathParser;
import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.ensureMaxByteSize;
import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.safeSubView;

public record IntElement(int size, boolean signed, String expression) implements SchemaElement {

    public IntElement {
        ensureMaxByteSize(size, 8);
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, size);
        long l = view.parseInt(template.endianness());
        if(expression != null) {
            l = MathParser.evalPostfix(expression, signed, objects, view.toDataSegment(), l);
        }
        return new ParseStepResult(data, view, new IntData(l, signed));
    }
}
