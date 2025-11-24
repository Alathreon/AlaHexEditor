package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.BlobData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.*;

public record VariableSizeBlobElement(int fieldSize) implements SchemaElement {
    public VariableSizeBlobElement {
        ensureMaxByteSize(fieldSize, 4);
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, fieldSize);
        ByteView subView = safeSubView(data, fieldSize, (int)view.parseInt(template.endianness()));
        String utf8String = subView.toString();
        return new ParseStepResult(data, subView, new BlobData(utf8String));
    }
}
