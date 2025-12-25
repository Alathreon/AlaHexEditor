package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class ArrayElementTest {

    @Test
    void testFixedLength() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "array": {
                                    "@type": "ArrayElement",
                                    "lengthPolicy": {
                                        "@type": "FixedLengthPolicy",
                                        "length": 4
                                    },
                                    "schema": {
                                         "@type": "IntElement",
                                         "size": 1
                                    }
                                }
                            }
                        }""",
                "08060902",
                List.of(Pair.of("array", new ParseObject("08060902", new ArrayData(List.of(
                        new ParseObject("08060902", 0, 1, new IntData(8, false, 1)),
                        new ParseObject("08060902", 1, 1, new IntData(6, false, 1)),
                        new ParseObject("08060902", 2, 1, new IntData(9, false, 1)),
                        new ParseObject("08060902", 3, 1, new IntData(2, false, 1))
                )))))
        );
    }

    @Test
    void testFixedLengthLeftover() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "array": {
                                    "@type": "ArrayElement",
                                    "lengthPolicy": {
                                        "@type": "FixedLengthPolicy",
                                        "length": 4
                                    },
                                    "schema": {
                                        "@type": "IntElement",
                                        "size": 1
                                    }
                                },
                                "int_element": {
                                    "@type": "IntElement",
                                    "size": 1
                                }
                            }
                        }""",
                "0806090206",
                List.of(Pair.of("array", new ParseObject("08060902", new ArrayData(List.of(
                        new ParseObject("08060902", 0, 1, new IntData(8, false, 1)),
                        new ParseObject("08060902", 1, 1, new IntData(6, false, 1)),
                        new ParseObject("08060902", 2, 1, new IntData(9, false, 1)),
                        new ParseObject("08060902", 3, 1, new IntData(2, false, 1))
                )))),
                        Pair.of("int_element", new ParseObject("0806090206", 4, new IntData(6, false, 1))))
        );
    }

    @Test
    void testPrefixedLength() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "array": {
                                    "@type": "ArrayElement",
                                    "lengthPolicy": {
                                        "@type": "PrefixedLengthPolicy",
                                        "fieldSize": 1
                                    },
                                    "schema": {
                                        "@type": "IntElement",
                                        "size": 1
                                    }
                                }
                            }
                        }""",
                "0408060902",
                List.of(Pair.of("array", new ParseObject("0408060902", new ArrayData(List.of(
                        new ParseObject("0408060902", 1, 1, new IntData(8, false, 1)),
                        new ParseObject("0408060902", 2, 1, new IntData(6, false, 1)),
                        new ParseObject("0408060902", 3, 1, new IntData(9, false, 1)),
                        new ParseObject("0408060902", 4, 1, new IntData(2, false, 1))
                )))))
        );
    }

    @Test
    void testReferencedLength() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "array_size": {
                                   "@type": "IntElement",
                                   "size": 1
                                },
                                "array": {
                                    "@type": "ArrayElement",
                                    "lengthPolicy": {
                                        "@type": "ReferencedLengthPolicy",
                                        "sizeVarName": "array_size"
                                    },
                                    "schema": {
                                        "@type": "IntElement",
                                        "size": 1
                                    }
                                }
                            }
                        }""",
                "0408060902",
                List.of(Pair.of("array_size", new ParseObject("04", new IntData(4, false, 1))),
                        Pair.of("array", new ParseObject("0408060902", 1, new ArrayData(List.of(
                        new ParseObject("0408060902", 1, 1, new IntData(8, false, 1)),
                        new ParseObject("0408060902", 2, 1, new IntData(6, false, 1)),
                        new ParseObject("0408060902", 3, 1, new IntData(9, false, 1)),
                        new ParseObject("0408060902", 4, 1, new IntData(2, false, 1))
                )))))
        );
    }

    @Test
    void testPredicateTerminated() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "array": {
                                    "@type": "ArrayElement",
                                    "lengthPolicy": {
                                        "@type": "PredicateTerminatedPolicy",
                                        "against": 0,
                                        "operator": "NOT_EQUALS"
                                    },
                                    "schema": {
                                       "@type": "IntElement",
                                       "size": 1
                                    }
                                }
                            }
                        }""",
                "050203040100",
                List.of(Pair.of("array", new ParseObject("050203040100", new ArrayData(List.of(
                        new ParseObject("050203040100", 0, 1, new IntData(5, false, 1)),
                        new ParseObject("050203040100", 1, 1, new IntData(2, false, 1)),
                        new ParseObject("050203040100", 2, 1, new IntData(3, false, 1)),
                        new ParseObject("050203040100", 3, 1, new IntData(4, false, 1)),
                        new ParseObject("050203040100", 4, 1, new IntData(1, false, 1)),
                        new ParseObject("050203040100", 5, 1, new IntData(0, false, 1))
                )))))
        );
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
                                "array": {
                                    "@type": "ArrayElement",
                                    "lengthPolicy": {
                                        "@type": "PredicateTerminatedPolicy",
                                        "against": 0,
                                        "operator": "NOT_EQUALS",
                                        "structField": "b"
                                    },
                                    "schema": {
                                       "@type": "TypeElement",
                                       "name": "Pair"
                                    }
                                }
                            }
                        }""",
                "050203040100",
                List.of(Pair.of("array", new ParseObject("050203040100", new ArrayData(List.of(
                        new ParseObject("050203040100", 0, 2, StructData.from(List.of(
                                Pair.of("a", new ParseObject("050203040100",  0, 1, new IntData(5, false, 1))),
                                Pair.of("b", new ParseObject("050203040100",  1, 1, new IntData(2, false, 1)))
                        ))),
                        new ParseObject("050203040100", 2, 2, StructData.from(List.of(
                                Pair.of("a", new ParseObject("050203040100",  2, 1, new IntData(3, false, 1))),
                                Pair.of("b", new ParseObject("050203040100",  3, 1, new IntData(4, false, 1)))
                        ))),
                        new ParseObject("050203040100", 4, 2, StructData.from(List.of(
                                Pair.of("a", new ParseObject("050203040100",  4, 1, new IntData(1, false, 1))),
                                Pair.of("b", new ParseObject("050203040100",  5, 1, new IntData(0, false, 1)))
                        )))
                )))))
        );
    }
}
