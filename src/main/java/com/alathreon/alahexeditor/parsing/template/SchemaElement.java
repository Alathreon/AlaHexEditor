package com.alathreon.alahexeditor.parsing.template;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.Data;
import com.alathreon.alahexeditor.parsing.template.element.*;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayReferenceElement.class),
        @JsonSubTypes.Type(value = DynamicSizeArrayElement.class),
        @JsonSubTypes.Type(value = VariableSizeArrayElement.class),
        @JsonSubTypes.Type(value = VariableRefSizeArrayElement.class),
        @JsonSubTypes.Type(value = VariableSizeStringElement.class),
        @JsonSubTypes.Type(value = NullEndStringElement.class),
        @JsonSubTypes.Type(value = FixedSizeBlobElement.class),
        @JsonSubTypes.Type(value = FixedSizeStringElement.class),
        @JsonSubTypes.Type(value = FixedSizeArrayElement.class),
        @JsonSubTypes.Type(value = ComputedStringFormatElement.class),
        @JsonSubTypes.Type(value = ComputedIntElement.class),
        @JsonSubTypes.Type(value = IntElement.class),
        @JsonSubTypes.Type(value = FloatElement.class),
        @JsonSubTypes.Type(value = TypeElement.class),
})
public interface SchemaElement {
    ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException;
}
