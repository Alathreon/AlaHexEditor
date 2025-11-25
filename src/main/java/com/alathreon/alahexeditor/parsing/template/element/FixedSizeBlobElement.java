package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.deserializer.IntegerDeserializer;
import com.alathreon.alahexeditor.parsing.object.BlobData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.*;

public record FixedSizeBlobElement(@JsonDeserialize(using = IntegerDeserializer.class) int size) implements SchemaElement {
    public FixedSizeBlobElement {
        ensureNonNegative(size);
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, size);
        String utf8String = view.toString();
        return new ParseStepResult(data, view, new BlobData(utf8String));
    }
}
