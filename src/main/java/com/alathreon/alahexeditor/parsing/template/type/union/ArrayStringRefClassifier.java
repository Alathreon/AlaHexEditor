package com.alathreon.alahexeditor.parsing.template.type.union;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.Objects;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.*;

public record ArrayStringRefClassifier(String variable, int size, boolean zeroIsNull) implements Classifier {
    public ArrayStringRefClassifier {
        Objects.requireNonNull(variable, "variable cannot be null");
        ensureMaxByteSize(size, 4);
    }

    @Override
    public ClassifierResult find(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, size());
        int index = (int) view.parseInt(template.endianness());
        ParseObject parseObject = objects.get(variable);
        if(parseObject == null) throw new ParseException(data, "Expected array data for variable %s, but got: null".formatted(variable));
        parseObject = parseObject.resolveReferences();
        if(!(parseObject.data() instanceof ArrayData(var innerObjects))) throw new ParseException(data, "Expected array data for variable %s, but got: %s".formatted(variable, parseObject.data().getClass().getSimpleName()));
        if(zeroIsNull) {
            if(index == 0) {
                return new ClassifierResult(null, new NullData(), new IntData(0, false, size), view, data.leftover(view));
            } else {
                index--;
            }
        }
        if(index < 0 || index >= innerObjects.size()) throw new ParseException(view, "Error in Union %s: index %d not found in array %s".formatted(thisName, index, variable));
        Data structNameObject = innerObjects.get(index).data();
        if(!(structNameObject instanceof StringData(String val))) throw new ParseException(view, "Error in Union %s: element found in array %s at index %d is not a string: %s".formatted(thisName, variable, index, structNameObject.type()));
        return new ClassifierResult(val, structNameObject, new IntData(size, false, size), view, data.leftover(view));
    }
}
