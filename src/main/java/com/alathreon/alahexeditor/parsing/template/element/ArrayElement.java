package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.ArrayData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.PreparedLengthPolicy;
import com.alathreon.alahexeditor.util.ByteView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record ArrayElement(LengthPolicy lengthPolicy, SchemaElement schema) implements SchemaElement {
    public ArrayElement {
        Objects.requireNonNull(lengthPolicy);
        Objects.requireNonNull(schema);
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        PreparedLengthPolicy preparedLengthPolicy = lengthPolicy.prepare(thisName, data, template, objects);
        ByteView view = preparedLengthPolicy.leftover();
        List<ParseObject> result = new ArrayList<>();
        objects.startScope();
        ArrayData arrayData = new ArrayData(result);
        objects.add(thisName, new ParseObject(view, arrayData));
        ParseObject self = null;
        while(preparedLengthPolicy.condition().hasNext(self, thisName, data, template, objects)) {
            ParseStepResult stepResult = schema.parse(thisName, view, template, objects);
            view = stepResult.leftover();
            result.add(stepResult.object());
            self = stepResult.object();
        }
        objects.endScope();
        return new ParseStepResult(data, data.subView(view.offset() - data.offset()), arrayData);
    }
}
