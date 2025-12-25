package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.object.StringData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.PreparedLengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl.NullTerminatedStringPolicy;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record StringElement(LengthPolicy lengthPolicy, Charset charset, boolean stopAtNull) implements SchemaElement {

    @JsonCreator
    public static StringElement jsonCreator(
            @JsonProperty("lengthPolicy") LengthPolicy lengthPolicy, @JsonProperty("charset") Charset charset, @JsonProperty("stopAtNull") Boolean stopAtNull) {
        return new StringElement(lengthPolicy, charset, stopAtNull == null || stopAtNull);
    }

    public StringElement {
        Objects.requireNonNull(lengthPolicy);
        if(charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        if(lengthPolicy instanceof NullTerminatedStringPolicy p) {
            p.setCharset(charset);
        }
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        PreparedLengthPolicy preparedLengthPolicy = lengthPolicy.prepare(thisName, data, template, objects);
        ByteView view = preparedLengthPolicy.leftover();
        ParseObject self = null;
        int i = 0;
        while(preparedLengthPolicy.condition().hasNext(self, thisName, data, template, objects)) {
            ByteView currentView = view.subView(i, 1);
            self = new ParseObject(currentView,
                new IntData(currentView.parseInt(template.endianness()), false, 1));
            i++;
        }
        view = view.subView(i);
        String string = view.toTextString(charset, stopAtNull);
        return new ParseStepResult(data, view, new StringData(string));
    }
}
