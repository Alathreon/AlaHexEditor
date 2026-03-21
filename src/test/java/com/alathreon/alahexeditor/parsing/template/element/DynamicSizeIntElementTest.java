package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class DynamicSizeIntElementTest {

    @Test
    void testULEB128() {
        ParserTester parserTester = new ParserTester();
        var schema = """
                    {
                        "schema": {
                            "int_element": {
                                "@type": "DynamicSizeIntElement",
                                "encoding": "ULEB128"
                            }
                        }
                    }""";
        parserTester.test(schema,
                "05",
                List.of(Pair.of("int_element", new ParseObject("05", new IntData(5, false, 1))))
        );
        parserTester.test(schema,
                "7F",
                List.of(Pair.of("int_element", new ParseObject("7F", new IntData(127, false, 1))))
        );
        parserTester.test(schema,
                "8001",
                List.of(Pair.of("int_element", new ParseObject("8001", new IntData(128, false, 1))))
        );
        parserTester.test(schema,
                "FF01",
                List.of(Pair.of("int_element", new ParseObject("FF01", new IntData(255, false, 1))))
        );
        parserTester.test(schema,
                "8002",
                List.of(Pair.of("int_element", new ParseObject("8002", new IntData(256, false, 2))))
        );
        parserTester.test(schema,
                "E58E26",
                List.of(Pair.of("int_element", new ParseObject("E58E26", new IntData(624485, false, 3))))
        );
        parserTester.test("""
                {
                    "schema": {
                        "int_dyn_element": {
                            "@type": "DynamicSizeIntElement",
                            "encoding": "ULEB128"
                        },
                        "int_element": {
                            "@type": "IntElement",
                            "size": 1
                        }
                    }
                }""",
                "E58E2609",
                List.of(
                        Pair.of("int_dyn_element", new ParseObject("E58E26", new IntData(624485, false, 3))),
                        Pair.of("int_element", new ParseObject("E58E2609", 3, new IntData(9, false, 1)))
                )
        );
    }
}
