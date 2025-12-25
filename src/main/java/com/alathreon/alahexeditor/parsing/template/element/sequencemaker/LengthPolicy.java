package com.alathreon.alahexeditor.parsing.template.element.sequencemaker;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl.FixedLengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl.NullTerminatedStringPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl.PredicateTerminatedPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl.PrefixedLengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl.ReferencedLengthPolicy;
import com.alathreon.alahexeditor.util.ByteView;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedLengthPolicy.class),
        @JsonSubTypes.Type(value = PrefixedLengthPolicy.class),
        @JsonSubTypes.Type(value = ReferencedLengthPolicy.class),
        @JsonSubTypes.Type(value = PredicateTerminatedPolicy.class),
        @JsonSubTypes.Type(value = NullTerminatedStringPolicy.class)
})
public interface LengthPolicy {
    PreparedLengthPolicy prepare(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException;
}
