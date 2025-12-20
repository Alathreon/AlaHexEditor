package com.alathreon.alahexeditor.parsing.template.type;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.BitsetData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class BitsetTypeTest {
    @Test
    void test() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "types": {
                                "Color": {
                                    "@type": "BitsetType",
                                    "size": 1,
                                    "names": [
                                        "Black", "White"
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
                List.of(Pair.of("color", new ParseObject("01", new BitsetData("00000001", List.of("Black")))))
        );
        parserTester.test(
                """
                        {
                            "types": {
                                "Color": {
                                    "@type": "BitsetType",
                                    "size": 1,
                                    "names": [
                                        "Black", "White"
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
                "02",
                List.of(Pair.of("color", new ParseObject("02", new BitsetData("00000010", List.of("White")))))
        );
        parserTester.test(
                """
                        {
                            "types": {
                                "Color": {
                                    "@type": "BitsetType",
                                    "size": 1,
                                    "names": [
                                        "Black", "White"
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
                "03",
                List.of(Pair.of("color", new ParseObject("03", new BitsetData("00000011", List.of("Black", "White")))))
        );
    }
}