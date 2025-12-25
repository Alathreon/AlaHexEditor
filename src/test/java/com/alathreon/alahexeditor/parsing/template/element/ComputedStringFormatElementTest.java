package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.object.StringData;
import com.alathreon.alahexeditor.parsing.object.StructData;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class ComputedStringFormatElementTest {

    @Test
    void testVariables() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "name": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "NullTerminatedStringPolicy"
                                    }
                                },
                                "age": {
                                    "@type": "IntElement",
                                    "size": 1
                                },
                                "display": {
                                    "@type": "ComputedStringFormatElement",
                                    "format": "Name: %s, age: %d.",
                                    "variables": [ [ "name" ], [ "age" ] ]
                                }
                            }
                        }""",
                "416E61000F",
                List.of(Pair.of("name", new ParseObject("416E61000F", 0, 4, new StringData("Ana"))),
                        Pair.of("age", new ParseObject("416E61000F", 4, 1, new IntData(15, false, 1))),
                        Pair.of("display", new ParseObject("416E6100", new StringData("Name: Ana, age: 15."))))
        );
    }

    @Test
    void testStruct() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "types": {
                                "Person": {
                                    "@type": "StructType",
                                    "members": {
                                        "name": {
                                            "@type": "StringElement",
                                            "lengthPolicy": {
                                                "@type": "NullTerminatedStringPolicy"
                                            }
                                        },
                                        "age": {
                                            "@type": "IntElement",
                                            "size": 1
                                        }
                                    }
                                }
                            },
                            "schema": {
                                "person": {
                                    "@type": "TypeElement",
                                    "name": "Person"
                                },
                                "display": {
                                    "@type": "ComputedStringFormatElement",
                                    "format": "Name: %s, age: %d.",
                                    "variables": [ [ "person", "name" ], [ "person", "age" ] ]
                                }
                            }
                        }""",
                "416E61000F",
                List.of(Pair.of("person", new ParseObject("416E61000F", StructData.from(List.of(
                                Pair.of("name", new ParseObject("416E61000F", 0, 4, new StringData("Ana"))),
                                Pair.of("age", new ParseObject("416E61000F", 4, 1, new IntData(15, false, 1)))
                            )))),
                        Pair.of("display", new ParseObject("416E61000F", new StringData("Name: Ana, age: 15."))))
        );
    }
}
