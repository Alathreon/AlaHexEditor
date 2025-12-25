package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class ArrayReferenceElementTest {

    @Test
    void test() {
        String json = """
                        {
                            "schema": {
                                "array": {
                                    "@type": "ArrayElement",
                                    "lengthPolicy": {
                                        "@type": "PrefixedLengthPolicy",
                                        "fieldSize": 1
                                    },
                                    "schema": {
                                        "@type": "StringElement",
                                        "lengthPolicy": {
                                            "@type": "NullTerminatedStringPolicy"
                                        }
                                    }
                                },
                                 "reference": {
                                     "@type": "ArrayReferenceElement",
                                     "size": 1,
                                     "variable": "array"
                                 }
                            }
                        }""";
        ParserTester parserTester = new ParserTester();
        parserTester.test(json,
                "02496e7465676572456e74727900537472696e67456e7472790000",
                List.of(Pair.of("array", new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", new ArrayData(List.of(
                                new ParseObject("02496e7465676572456e74727900", 1, 13, new StringData("IntegerEntry")),
                                new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", 14, 12, new StringData("StringEntry"))
                        )))),
                        Pair.of("reference", new ParseObject("02496e7465676572456e74727900537472696e67456e7472790000", 26, new ArrayRefData(0,
                                new ParseObject("02496e7465676572456e74727900537472696e67456e7472790000", 1, 13, new StringData("IntegerEntry")))))
                )
        );
        parserTester.test(json,
                "02496e7465676572456e74727900537472696e67456e7472790001",
                List.of(Pair.of("array", new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", new ArrayData(List.of(
                        new ParseObject("02496e7465676572456e74727900", 1, 13, new StringData("IntegerEntry")),
                        new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", 14, 12, new StringData("StringEntry"))
                )))),
                        Pair.of("reference", new ParseObject("02496e7465676572456e74727900537472696e67456e7472790001", 26, new ArrayRefData(1,
                                new ParseObject("02496e7465676572456e74727900537472696e67456e7472790001", 14, 12, new StringData("StringEntry")))))
                )
        );
    }

    @Test
    void testZeroIsNull() {
        String json = """
                        {
                            "schema": {
                                "array": {
                                    "@type": "ArrayElement",
                                    "lengthPolicy": {
                                        "@type": "PrefixedLengthPolicy",
                                        "fieldSize": 1
                                    },
                                    "schema": {
                                        "@type": "StringElement",
                                        "lengthPolicy": {
                                            "@type": "NullTerminatedStringPolicy"
                                        }
                                    }
                                },
                                 "reference": {
                                     "@type": "ArrayReferenceElement",
                                     "size": 1,
                                     "variable": "array",
                                     "zeroIsNull": true
                                 }
                            }
                        }""";
        ParserTester parserTester = new ParserTester();
        parserTester.test(json,
                "02496e7465676572456e74727900537472696e67456e7472790000",
                List.of(Pair.of("array", new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", new ArrayData(List.of(
                                new ParseObject("02496e7465676572456e74727900", 1, 13, new StringData("IntegerEntry")),
                                new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", 14, 12, new StringData("StringEntry"))
                        )))),
                        Pair.of("reference", new ParseObject("02496e7465676572456e74727900537472696e67456e7472790000", 26, new NullData()))
                )
        );
        parserTester.test(json,
                "02496e7465676572456e74727900537472696e67456e7472790001",
                List.of(Pair.of("array", new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", new ArrayData(List.of(
                                new ParseObject("02496e7465676572456e74727900", 1, 13, new StringData("IntegerEntry")),
                                new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", 14, 12, new StringData("StringEntry"))
                        )))),
                        Pair.of("reference", new ParseObject("02496e7465676572456e74727900537472696e67456e7472790001", 26, new ArrayRefData(0,
                                new ParseObject("02496e7465676572456e74727900537472696e67456e7472790001", 1, 13, new StringData("IntegerEntry")))))
                )
        );
    }
}
