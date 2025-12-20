package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.object.StringData;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class StringElementTest {

    @Test
    void testNullEnd() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "NullEndStringElement"
                                }
                            }
                        }""",
                "416E6100",
                List.of(Pair.of("string", new ParseObject("416E6100", new StringData("Ana"))))
        );
    }

    @Test
    void testNullEndSJISCharset() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "NullEndStringElement",
                                    "charset": "SJIS"
                                }
                            }
                        }""",
                "82B182F182C982BF82CD00",
                List.of(Pair.of("string", new ParseObject("82B182F182C982BF82CD00", new StringData("こんにちは"))))
        );
    }

    @Test
    void testDoubleNullEnd() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "s1": {
                                    "@type": "NullEndStringElement"
                                },
                                "s2": {
                                    "@type": "NullEndStringElement"
                                }
                            }
                        }""",
                "496e7465676572456e74727900537472696e67456e74727900",
                List.of(Pair.of("s1", new ParseObject("496e7465676572456e74727900", 0, 13, new StringData("IntegerEntry"))),
                        Pair.of("s2",new ParseObject("496e7465676572456e74727900537472696e67456e74727900", 13, 12, new StringData("StringEntry"))))
        );
    }

    @Test
    void testFixedSize() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "FixedSizeStringElement",
                                    "size": 3
                                }
                            }
                        }""",
                "416E61",
                List.of(Pair.of("string", new ParseObject("416E61", new StringData("Ana"))))
        );
    }

    @Test
    void testFixedSizeLeftover() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "FixedSizeStringElement",
                                    "size": 5,
                                    "stopAtNull": true
                                }
                            }
                        }""",
                "416E610000",
                List.of(Pair.of("string", new ParseObject("416E610000", new StringData("Ana"))))
        );
    }

    @Test
    void testVariableSize() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                   "@type": "VariableSizeStringElement",
                                   "fieldSize": 1
                                }
                            }
                        }""",
                "03416E61",
                List.of(Pair.of("string", new ParseObject("03416E61", 1, new StringData("Ana"))))
        );
    }

    @Test
    void testVariableRefSize() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string_size": {
                                    "@type": "IntElement",
                                    "size": 1
                                },
                                "string": {
                                    "@type": "VariableRefSizeStringElement",
                                    "sizeVarName": "string_size"
                                }
                            }
                        }""",
                "03416E61",
                List.of(
                        Pair.of("string_size", new ParseObject("03", new IntData(3, false, 1))),
                        Pair.of("string", new ParseObject("03416E61", 1, new StringData("Ana")))
                )
        );
    }
}
