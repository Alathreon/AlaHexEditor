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
    void testNullTerminator() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "NullTerminatedStringPolicy"
                                    }
                                }
                            }
                        }""",
                "416E6100",
                List.of(Pair.of("string", new ParseObject("416E6100", new StringData("Ana"))))
        );
    }

    @Test
    void testNullTerminatorSJISCharset() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "NullTerminatedStringPolicy"
                                    },
                                    "charset": "SJIS"
                                }
                            }
                        }""",
                "82B182F182C982BF82CD00",
                List.of(Pair.of("string", new ParseObject("82B182F182C982BF82CD00", new StringData("こんにちは"))))
        );
    }

    @Test
    void testNullTerminatorUTF16LECharset() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "NullTerminatedStringPolicy"
                                    },
                                    "charset": "UTF_16LE"
                                }
                            }
                        }""",
                "4300530175007200",
                List.of(Pair.of("string", new ParseObject("4300530175007200", new StringData("Cœur"))))
        );
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "NullTerminatedStringPolicy"
                                    },
                                    "charset": "UTF_16LE"
                                }
                            }
                        }""",
                "4300530175007200",
                List.of(Pair.of("string", new ParseObject("4300530175007200", new StringData("Cœur"))))
        );
    }

    @Test
    void testDoubleNullTerminator() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "s1": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "NullTerminatedStringPolicy"
                                    }
                                },
                                "s2": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "NullTerminatedStringPolicy"
                                    }
                                }
                            }
                        }""",
                "496e7465676572456e74727900537472696e67456e74727900",
                List.of(Pair.of("s1", new ParseObject("496e7465676572456e74727900", 0, 13, new StringData("IntegerEntry"))),
                        Pair.of("s2",new ParseObject("496e7465676572456e74727900537472696e67456e74727900", 13, 12, new StringData("StringEntry"))))
        );
    }

    @Test
    void testFixedLength() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "FixedLengthPolicy",
                                        "length": 3
                                    }
                                }
                            }
                        }""",
                "416E61",
                List.of(Pair.of("string", new ParseObject("416E61", new StringData("Ana"))))
        );
    }

    @Test
    void testFixedLengthStopAtNull() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "FixedLengthPolicy",
                                        "length": 5
                                    }
                                }
                            }
                        }""",
                "416E610000",
                List.of(Pair.of("string", new ParseObject("416E610000", new StringData("Ana"))))
        );
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "FixedLengthPolicy",
                                        "length": 5
                                    },
                                    "stopAtNull": true
                                }
                            }
                        }""",
                "416E610000",
                List.of(Pair.of("string", new ParseObject("416E610000", new StringData("Ana"))))
        );
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "FixedLengthPolicy",
                                        "length": 5
                                    },
                                    "stopAtNull": false
                                }
                            }
                        }""",
                "416E610000",
                List.of(Pair.of("string", new ParseObject("416E610000", new StringData("Ana\0\0"))))
        );
    }

    @Test
    void testPrefixedLength() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "string": {
                                   "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "PrefixedLengthPolicy",
                                        "fieldSize": 1
                                    }
                                }
                            }
                        }""",
                "03416E61",
                List.of(Pair.of("string", new ParseObject("03416E61", 1, new StringData("Ana"))))
        );
    }

    @Test
    void testReferencedLength() {
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
                                    "@type": "StringElement",
                                    "lengthPolicy": {
                                        "@type": "ReferencedLengthPolicy",
                                        "sizeVarName": "string_size"
                                    }
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
