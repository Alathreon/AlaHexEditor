package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class ComputedIntElementTest {

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
                                },
                                "display": {
                                    "@type": "ComputedIntElement",
                                    "variables": [ "int_element" ]
                                }
                            }
                        }""",
                "05",
                List.of(Pair.of("int_element", new ParseObject("05", new IntData(5, false, 1))),
                        Pair.of("display", new ParseObject("05", new IntData(5, false, 1))))
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
                                    "size": 1
                                },
                                "display": {
                                    "@type": "ComputedIntElement",
                                    "variables": [ "int_element" ],
                                    "expression": "x 3 *"
                                }
                            }
                        }""",
                "05",
                List.of(Pair.of("int_element", new ParseObject("05", new IntData(5, false, 1))),
                        Pair.of("display", new ParseObject("05", new IntData(15, false, 1))))
        );
    }

    @Test
    void testStruct() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "types": {
                                "Pair": {
                                    "@type": "StructType",
                                    "members": {
                                        "a": {
                                            "@type": "IntElement",
                                            "size": 1
                                        },
                                        "b": {
                                            "@type": "IntElement",
                                            "size": 1
                                        }
                                    }
                                }
                            },
                            "schema": {
                                "pair": {
                                    "@type": "TypeElement",
                                    "name": "Pair"
                                },
                                "display": {
                                    "@type": "ComputedIntElement",
                                    "variables": [ "pair", "b" ]
                                }
                            }
                        }""",
                "0205",
                List.of(Pair.of("pair", new ParseObject("0205", StructData.from(List.of(
                                Pair.of("a", new ParseObject("02", 0, new IntData(2, false, 1))),
                                Pair.of("b", new ParseObject("0205", 1, new IntData(5, false, 1)))
                            )))),
                        Pair.of("display", new ParseObject("0205", new IntData(5, false, 1))))
        );
    }
}
