package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.SchemaType;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.*;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.*;

public record TypeElement(String name) implements SchemaElement {
    public TypeElement {
        Objects.requireNonNull(name);
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        SchemaType<?> type = findType(template, data, name);
        SchemaType.ParseTypeResult<?> result = type.parseData(name, data, template, objects);
        return new ParseStepResult(result.parent(), result.segment(), result.data());
    }
}
