package com.alathreon.alahexeditor.parsing.template.type.union;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.EnumData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaType;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.type.EnumType;
import com.alathreon.alahexeditor.parsing.template.type.UnionType;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.Objects;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.findType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ArrayStringRefClassifier.class),
        @JsonSubTypes.Type(value = EnumClassifier.class),
        @JsonSubTypes.Type(value = SelfEnumClassifier.class),
        @JsonSubTypes.Type(value = IntRangeClassifier.class)
})
public interface Classifier {
    ClassifierResult find(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException;
}
