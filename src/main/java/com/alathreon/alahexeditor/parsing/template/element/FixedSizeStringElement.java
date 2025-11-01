package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.StringData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.ensureMaxByteSize;
import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.safeSubView;

public record FixedSizeStringElement(int size, Charset charset) implements SchemaElement {
    public FixedSizeStringElement {
        ensureMaxByteSize(size, 4);
        if(charset == null) {
            charset = StandardCharsets.UTF_8;
        }
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, size);
        String string = view.toTextString(charset);
        return new ParseStepResult(data, view, new StringData(string));
    }
}
