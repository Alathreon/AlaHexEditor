package com.alathreon.alahexeditor.parsing;

import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.util.Pair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTester {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Parser parser = new Parser();

    public void test(String templateJson, String hex, List<Pair<String, ParseObject>> expected) {
        try {
            Template template = mapper.readValue(templateJson, Template.class);
            List<Pair<String, ParseObject>> parse = parser.parse(template, ByteView.fromHexString(hex));
            assertEquals(expected, parse);
        } catch (JsonProcessingException | ParseException e) {
            fail(e);
        }
    }
}