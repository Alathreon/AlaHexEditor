package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.BlobData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.PreparedLengthPolicy;
import com.alathreon.alahexeditor.util.ByteView;
import java.util.Objects;

public record BlobElement(LengthPolicy lengthPolicy) implements SchemaElement {
    public BlobElement {
        Objects.requireNonNull(lengthPolicy);
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
                new BlobData(currentView.toString()));
            i++;
        }
        view = view.subView(i);
        String hex = view.toString();
        return new ParseStepResult(data, view, new BlobData(hex));
    }
}
