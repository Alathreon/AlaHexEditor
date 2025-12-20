package com.alathreon.alahexeditor.parsing.template.type;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.object.StringData;
import com.alathreon.alahexeditor.parsing.object.StructData;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class StructTypeTest {
    @Test
    void test() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "types": {
                                "Person": {
                                    "@type": "StructType",
                                    "members": {
                                        "name": {
                                            "@type": "NullEndStringElement"
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
                                }
                            }
                        }""",
                "416E61000F",
                List.of(Pair.of("person", new ParseObject("416E61000F", StructData.from(List.of(
                                Pair.of("name", new ParseObject("416E61000F", 0, 4, new StringData("Ana"))),
                                Pair.of("age", new ParseObject("416E61000F", 4, 1, new IntData(15, false, 1)))
                        )))))
        );
    }
}