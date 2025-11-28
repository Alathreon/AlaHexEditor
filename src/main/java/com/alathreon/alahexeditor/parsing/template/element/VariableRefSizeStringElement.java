package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.object.StringData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.safeSubView;

public record VariableRefSizeStringElement(String sizeVarName, Charset charset, boolean stopAtNull) implements SchemaElement {

    @JsonCreator
    public static VariableRefSizeStringElement jsonCreator(
            @JsonProperty("sizeVarName") String sizeVarName, @JsonProperty("charset") Charset charset, @JsonProperty("stopAtNull") Boolean stopAtNull) {
        return new VariableRefSizeStringElement(sizeVarName, charset, stopAtNull != null && stopAtNull);
    }

    public VariableRefSizeStringElement {
        Objects.requireNonNull(sizeVarName);
        if(charset == null) {
            charset = StandardCharsets.UTF_8;
        }
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ParseObject parseObject = objects.get(sizeVarName);
        if(parseObject == null) throw new ParseException(data, "Expected string size data for variable %s, but got: null".formatted(sizeVarName));
        parseObject = parseObject.resolveReferences();
        if(!(parseObject.data() instanceof IntData(var size, _, _))) throw new ParseException(data, "Expected string size data for variable %s, but got: %s".formatted(sizeVarName, parseObject.data().getClass().getSimpleName()));
        if(size.compareTo(BigInteger.ZERO) < 0) throw new ParseException(data, "Array size negative: " + size);
        if(size.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) throw new ParseException(data, "Array size too large: " + size);
        ByteView subView = safeSubView(data, size.intValue());
        String string = subView.toTextString(charset, stopAtNull);
        return new ParseStepResult(data, subView, new StringData(string));
    }
}
