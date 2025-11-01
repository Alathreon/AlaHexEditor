package com.alathreon.alahexeditor.parsing;

import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.util.Pair;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Parser {
    public static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^\\w+$");
    public List<Pair<String, ParseObject>> parse(Template template, ByteView data) throws ParseException {
        Map<String, ParseObject> result = new LinkedHashMap<>();
        ParseObjects parseObjects = new ParseObjects(result);
        for (Map.Entry<String, SchemaElement> type : template.schema().entrySet()) {
            ParseStepResult stepResult = type.getValue().parse("TreeItemRoot", data, template, parseObjects);
            data = stepResult.leftover();
            result.put(type.getKey(), stepResult.object());
        }
        return result.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue())).toList();
    }
}
