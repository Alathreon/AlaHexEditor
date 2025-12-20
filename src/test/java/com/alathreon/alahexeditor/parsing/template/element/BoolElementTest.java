package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.BoolData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class BoolElementTest {
    @Test
    void testSimple() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "bool_element": {
                                    "@type": "BoolElement"
                                }
                            }
                        }""",
                "00",
                List.of(Pair.of("bool_element", new ParseObject("00", new BoolData(false))))
        );
        parserTester.test(
                """
                        {
                            "schema": {
                                "bool_element": {
                                    "@type": "BoolElement"
                                }
                            }
                        }""",
                "01",
                List.of(Pair.of("bool_element", new ParseObject("01", new BoolData(true))))
        );
        parserTester.test(
                """
                        {
                            "schema": {
                                "bool_element": {
                                    "@type": "BoolElement"
                                }
                            }
                        }""",
                "02",
                List.of(Pair.of("bool_element", new ParseObject("02", new BoolData(true))))
        );
    }
}