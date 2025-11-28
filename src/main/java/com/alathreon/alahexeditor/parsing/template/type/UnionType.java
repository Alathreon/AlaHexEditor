package com.alathreon.alahexeditor.parsing.template.type;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.object.StructData;
import com.alathreon.alahexeditor.parsing.object.UnionData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaType;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.type.union.Classifier;
import com.alathreon.alahexeditor.parsing.template.type.union.ClassifierResult;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.Objects;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.findType;

public record UnionType(Classifier classifier) implements SchemaType<UnionData> {

    public UnionType {
        Objects.requireNonNull(classifier, "classifier cannot be null");
    }

    @Override
    public ParseTypeResult<UnionData> parseData(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ClassifierResult classifierResult = classifier.find(thisName, data, template, objects);
        String structName = classifierResult.structName();
        ByteView leftover = classifierResult.leftover();

        if(structName == null) {
            ParseObject parseObject = new ParseObject(classifierResult.segment(), classifierResult.data());
            return new ParseTypeResult<>(data, classifierResult.segment(), new UnionData(
                    parseObject,
                    classifierResult.intData(),
                    parseObject
            ));
        }
        StructType structType = findType(template, leftover, structName, StructType.class);

        objects.startScope();
        objects.add(thisName, new ParseObject(classifierResult.segment().toDataSegment(), classifierResult.intData()));
        ParseTypeResult<StructData> structResult = structType.parseData(structName, leftover, template, objects);
        objects.endScope();

        ByteView segment = data.subView(0, classifierResult.segment().length() + structResult.segment().length());
        ParseObject classifierObject = new ParseObject(classifierResult.segment().toDataSegment(), classifierResult.data());
        ParseObject dataObject = new ParseObject(structResult.segment().toDataSegment(), structResult.data());
        return new ParseTypeResult<>(data, segment, new UnionData(classifierObject, classifierResult.intData(), dataObject));
    }
}
