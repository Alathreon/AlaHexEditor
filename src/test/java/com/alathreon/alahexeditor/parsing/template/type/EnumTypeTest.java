package com.alathreon.alahexeditor.parsing.template.type;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class EnumTypeTest {
    @Test
    void test() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "types": {
                                "Color": {
                                    "@type": "EnumType",
                                    "size": 1,
                                    "constants": [
                                        { "key":  0, "value":  "Black" },
                                        { "key":  1, "value":  "White" }
                                    ]
                                }
                            },
                            "schema": {
                                "color": {
                                    "@type": "TypeElement",
                                    "name": "Color"
                                }
                            }
                        }""",
                "00",
                List.of(Pair.of("color", new ParseObject("00", new EnumData(0, 1, "Black"))))
        );
        parserTester.test(
                """
                        {
                            "types": {
                                "Color": {
                                    "@type": "EnumType",
                                    "size": 1,
                                    "constants": [
                                        { "key":  0, "value":  "Black" },
                                        { "key":  1, "value":  "White" }
                                    ]
                                }
                            },
                            "schema": {
                                "color": {
                                    "@type": "TypeElement",
                                    "name": "Color"
                                }
                            }
                        }""",
                "01",
                List.of(Pair.of("color", new ParseObject("01", new EnumData(1, 1, "White"))))
        );
    }
}