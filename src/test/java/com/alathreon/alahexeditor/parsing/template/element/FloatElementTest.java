package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.FloatData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class FloatElementTest {

    @Test
    void testSimple() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "float_element": {
                                    "@type": "FloatElement",
                                    "size": 4
                                }
                            }
                        }""",
                "42280000",
                List.of(Pair.of("float_element", new ParseObject("42280000", new FloatData(42, 4))))
        );
    }
}
