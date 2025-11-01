package com.alathreon.alahexeditor.parsing.template;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.Data;
import com.alathreon.alahexeditor.parsing.template.type.BitsetType;
import com.alathreon.alahexeditor.parsing.template.type.EnumType;
import com.alathreon.alahexeditor.parsing.template.type.StructType;
import com.alathreon.alahexeditor.parsing.template.type.UnionType;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EnumType.class),
        @JsonSubTypes.Type(value = StructType.class),
        @JsonSubTypes.Type(value = UnionType.class),
        @JsonSubTypes.Type(value = BitsetType.class),
})
public interface SchemaType<D extends Data> {
    record ParseTypeResult<D extends Data>(ByteView parent, ByteView segment, D data) {}
    ParseTypeResult<D> parseData(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException;
}
