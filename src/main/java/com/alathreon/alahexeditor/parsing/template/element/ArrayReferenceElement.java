package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.Objects;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.ensureMaxByteSize;
import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.safeSubView;

public record ArrayReferenceElement(String variable, int size, boolean zeroIsNull) implements SchemaElement {
    public ArrayReferenceElement {
        Objects.requireNonNull(variable);
        ensureMaxByteSize(size, 4);
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, size);
        int index = (int)view.parseInt(template.endianness());
        ParseObject parseObject = objects.get(variable);
        if(parseObject == null) throw new ParseException(data, "Expected array data for variable %s but got: null".formatted(variable));
        parseObject = parseObject.resolveReferences();
        if(!(parseObject.data() instanceof ArrayData(var innerObjects))) throw new ParseException(data, "Expected array data for variable %s but got: %s".formatted(variable, parseObject.data().getClass().getSimpleName()));
        if(zeroIsNull) {
            if(index == 0) {
                return new ParseStepResult(data, view, new NullData());
            } else {
                index--;
            }
        }
        if(index >= innerObjects.size()) throw new ParseException(data, "Index %d out of bounds for array length %d".formatted(index, innerObjects.size()));
        return new ParseStepResult(data, view, new ArrayRefData(index, innerObjects.get(index)));
    }
}
