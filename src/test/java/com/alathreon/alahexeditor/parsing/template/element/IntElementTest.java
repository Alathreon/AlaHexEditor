package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class IntElementTest {

    @Test
    void testSimple() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "int_element": {
                                    "@type": "IntElement",
                                    "size": 1
                                }
                            }
                        }""",
                "05",
                List.of(Pair.of("int_element", new ParseObject("05", new IntData(5, false, 1))))
        );
    }

    @Test
    void testSigned() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "int_element": {
                                    "@type": "IntElement",
                                    "size": 1,
                                    "signed": true
                                }
                            }
                        }""",
                "FF",
                List.of(Pair.of("int_element", new ParseObject("FF", new IntData(0xFF, true, 1))))
        );
    }

    @Test
    void testExpression() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                          "schema": {
                              "int_element": {
                                  "@type": "IntElement",
                                  "size": 1,
                                  "expression": "x 3 *"
                              }
                          }
                        }""",
                "05",
                List.of(Pair.of("int_element", new ParseObject("05", new IntData(15, false, 1))))
        );
    }
}
