package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.StringData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.ensureMaxByteSize;
import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.safeSubView;

public record VariableSizeStringElement(int fieldSize, Charset charset, boolean stopAtNull) implements SchemaElement {

    @JsonCreator
    public static VariableSizeStringElement jsonCreator(
            @JsonProperty("fieldSize") int fieldSize, @JsonProperty("charset") Charset charset, @JsonProperty("stopAtNull") Boolean stopAtNull) {
        return new VariableSizeStringElement(fieldSize, charset, stopAtNull != null && stopAtNull);
    }

    public VariableSizeStringElement {
        ensureMaxByteSize(fieldSize, 4);
        if(charset == null) {
            charset = StandardCharsets.UTF_8;
        }
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, fieldSize);
        ByteView subView = safeSubView(data, fieldSize, (int)view.parseInt(template.endianness()));
        String string = subView.toTextString(charset, stopAtNull);
        return new ParseStepResult(data, subView, new StringData(string));
    }
}
