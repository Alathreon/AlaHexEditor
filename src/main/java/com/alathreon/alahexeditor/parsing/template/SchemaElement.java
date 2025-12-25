package com.alathreon.alahexeditor.parsing.template;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.template.element.*;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayReferenceElement.class),
        @JsonSubTypes.Type(value = BlobElement.class),
        @JsonSubTypes.Type(value = StringElement.class),
        @JsonSubTypes.Type(value = ArrayElement.class),
        @JsonSubTypes.Type(value = ComputedStringFormatElement.class),
        @JsonSubTypes.Type(value = ComputedIntElement.class),
        @JsonSubTypes.Type(value = IntElement.class),
        @JsonSubTypes.Type(value = FloatElement.class),
        @JsonSubTypes.Type(value = BoolElement.class),
        @JsonSubTypes.Type(value = TypeElement.class),
})
public interface SchemaElement {
    ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException;
}
