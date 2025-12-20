package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class BlobElementTest {

    @Test
    void testFixedSize() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "blob": {
                                    "@type": "FixedSizeBlobElement",
                                    "size": 4
                                }
                            }
                        }""",
                "416E61AF",
                List.of(Pair.of("blob", new ParseObject("416E61AF", new BlobData("41 6E 61 AF"))))
        );
    }

    @Test
    void testFixedSizeLeftover() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "blob": {
                                    "@type": "FixedSizeBlobElement",
                                    "size": 4
                                },
                                "int_element": {
                                    "@type": "IntElement",
                                    "size": 1
                                }
                            }
                        }""",
                "416E61AF06",
                List.of(Pair.of("blob", new ParseObject("416E61AF", new BlobData("41 6E 61 AF"))),
                        Pair.of("int_element", new ParseObject("416E61AF06", 4, new IntData(6, false, 1))))
        );
    }

    @Test
    void testVariableSize() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "blob": {
                                    "@type": "VariableSizeBlobElement",
                                    "fieldSize": 1
                                }
                            }
                        }""",
                "04416E61AF",
                List.of(Pair.of("blob", new ParseObject("04416E61AF", 1, new BlobData("41 6E 61 AF"))))
        );
    }

    @Test
    void testVariableRefSize() {
        ParserTester parserTester = new ParserTester();
        parserTester.test(
                """
                        {
                            "schema": {
                                "blob_size": {
                                   "@type": "IntElement",
                                   "size": 1
                                },
                                "blob": {
                                    "@type": "VariableRefSizeBlobElement",
                                    "sizeVarName": "blob_size"
                                }
                            }
                        }""",
                "04416E61AF",
                List.of(Pair.of("blob_size", new ParseObject("04", new IntData(4, false, 1))),
                        Pair.of("blob", new ParseObject("04416E61AF", 1, new BlobData("41 6E 61 AF"))))
        );
    }
}
