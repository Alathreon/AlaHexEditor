package com.alathreon.alahexeditor.parsing.template.element.sequencemaker;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

public interface LengthPolicyCondition {
    boolean hasNext(ParseObject self, String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException;
}
