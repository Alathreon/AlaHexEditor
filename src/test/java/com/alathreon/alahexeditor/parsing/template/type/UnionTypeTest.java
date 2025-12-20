package com.alathreon.alahexeditor.parsing.template.type;

import com.alathreon.alahexeditor.parsing.ParserTester;
import com.alathreon.alahexeditor.parsing.object.*;
import com.alathreon.alahexeditor.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

class UnionTypeTest {
    @Test
    void testSelfEnumClassifier() {
        String json = """
                        {
                            "types": {
                                "Entry": {
                                    "@type": "UnionType",
                                    "classifier": {
                                        "@type": "SelfEnumClassifier",
                                        "size": 1,
                                        "constants": [
                                            { "key":  0, "value":  "IntegerEntry" },
                                            { "key":  1, "value":  "StringEntry" }
                                        ]
                                    }
                                },
                                "IntegerEntry": {
                                    "@type": "StructType",
                                    "members": {
                                    "value": {
                                        "@type": "IntElement",
                                        "size": 1
                                        }
                                    }
                                },
                                "StringEntry": {
                                    "@type": "StructType",
                                    "members": {
                                        "value": {
                                            "@type": "NullEndStringElement"
                                        }
                                    }
                                }
                            },
                            "schema": {
                                "entry": {
                                    "@type": "TypeElement",
                                    "name": "Entry"
                                }
                            }
                        }""";
        ParserTester parserTester = new ParserTester();
        parserTester.test(json, "0005",
                List.of(Pair.of("entry", new ParseObject("0005", new UnionData(
                        new ParseObject("00", new EnumData(0, 1, "IntegerEntry")),
                        new IntData(0, false, 1),
                        new ParseObject("0005", 1, StructData.from(List.of(Pair.of("value", new ParseObject("0005", 1, new IntData(5, false, 1))))))
                ))))
        );
        parserTester.test(json, "01416E6100",
                List.of(Pair.of("entry", new ParseObject("01416E6100", new UnionData(
                        new ParseObject("01", new EnumData(1, 1, "StringEntry")),
                        new IntData(1, false, 1),
                        new ParseObject("01416E6100", 1, StructData.from(List.of(Pair.of("value", new ParseObject("01416E6100", 1, new StringData("Ana"))))))
                ))))
        );
    }

    @Test
    void testEnumClassifier() {
        String json = """
                        {
                            "types": {
                                "Entry": {
                                    "@type": "UnionType",
                                    "classifier": {
                                        "@type": "EnumClassifier",
                                         "enumClassifierName": "EntryKind"
                                    }
                                },
                                "EntryKind": {
                                    "@type": "EnumType",
                                    "size": 1,
                                    "constants": [
                                        { "key":  0, "value":  "IntegerEntry" },
                                        { "key":  1, "value":  "StringEntry" }
                                    ]
                                },
                                "IntegerEntry": {
                                    "@type": "StructType",
                                    "members": {
                                    "value": {
                                        "@type": "IntElement",
                                        "size": 1
                                        }
                                    }
                                },
                                "StringEntry": {
                                    "@type": "StructType",
                                    "members": {
                                        "value": {
                                            "@type": "NullEndStringElement"
                                        }
                                    }
                                }
                            },
                            "schema": {
                                "entry": {
                                    "@type": "TypeElement",
                                    "name": "Entry"
                                }
                            }
                        }""";
        ParserTester parserTester = new ParserTester();
        parserTester.test(json, "0005",
                List.of(Pair.of("entry", new ParseObject("0005", new UnionData(
                        new ParseObject("00", new EnumData(0, 1, "IntegerEntry")),
                        new IntData(0, false, 1),
                        new ParseObject("0005", 1, StructData.from(List.of(Pair.of("value", new ParseObject("0005", 1, new IntData(5, false, 1))))))
                ))))
        );
        parserTester.test(json, "01416E6100",
                List.of(Pair.of("entry", new ParseObject("01416E6100", new UnionData(
                        new ParseObject("01", new EnumData(1, 1, "StringEntry")),
                        new IntData(1, false, 1),
                        new ParseObject("01416E6100", 1, StructData.from(List.of(Pair.of("value", new ParseObject("01416E6100", 1, new StringData("Ana"))))))
                ))))
        );
    }

    @Test
    void testIntRangeClassifier() {
        String json = """
                        {
                            "types": {
                                "Entry": {
                                    "@type": "UnionType",
                                    "classifier": {
                                        "@type": "IntRangeClassifier",
                                        "size": 1,
                                        "rangeBindings": [
                                            { "from": 0, "to":  10, "name": "IntegerEntry" },
                                            { "from": 10, "to":  20, "name": "StringEntry" }
                                        ]
                                    }
                                },
                                "IntegerEntry": {
                                    "@type": "StructType",
                                    "members": {
                                    "value": {
                                        "@type": "IntElement",
                                        "size": 1
                                        }
                                    }
                                },
                                "StringEntry": {
                                    "@type": "StructType",
                                    "members": {
                                        "value": {
                                            "@type": "NullEndStringElement"
                                        }
                                    }
                                }
                            },
                            "schema": {
                                "entry": {
                                    "@type": "TypeElement",
                                    "name": "Entry"
                                }
                            }
                        }""";
        ParserTester parserTester = new ParserTester();
        parserTester.test(json, "0505",
                List.of(Pair.of("entry", new ParseObject("0505", new UnionData(
                        new ParseObject("05", new IntRangeBindingData("IntegerEntry", 0, 10)),
                        new IntData(5, false, 1),
                        new ParseObject("0505", 1, StructData.from(List.of(Pair.of("value", new ParseObject("0505", 1, new IntData(5, false, 1))))))
                ))))
        );
        parserTester.test(json, "0A416E6100",
                List.of(Pair.of("entry", new ParseObject("0A416E6100", new UnionData(
                        new ParseObject("0A", new IntRangeBindingData("StringEntry", 10, 20)),
                        new IntData(10, false, 1),
                        new ParseObject("0A416E6100", 1, StructData.from(List.of(Pair.of("value", new ParseObject("0A416E6100", 1, new StringData("Ana"))))))
                ))))
        );
        parserTester.test(json, "0F416E6100",
                List.of(Pair.of("entry", new ParseObject("0F416E6100", new UnionData(
                        new ParseObject("0F", new IntRangeBindingData("StringEntry", 10, 20)),
                        new IntData(15, false, 1),
                        new ParseObject("0F416E6100", 1, StructData.from(List.of(Pair.of("value", new ParseObject("0F416E6100", 1, new StringData("Ana"))))))
                ))))
        );
    }

    @Test
    void testArrayStringRefClassifierRaw() {
        String json = """
                        {
                            "types": {
                                "Entry": {
                                    "@type": "UnionType",
                                    "classifier": {
                                        "@type": "ArrayStringRefClassifier",
                                        "size": 1,
                                        "variable": "list"
                                    }
                                },
                                "IntegerEntry": {
                                    "@type": "StructType",
                                    "members": {
                                    "value": {
                                        "@type": "IntElement",
                                        "size": 1
                                        }
                                    }
                                },
                                "StringEntry": {
                                    "@type": "StructType",
                                    "members": {
                                        "value": {
                                            "@type": "NullEndStringElement"
                                        }
                                    }
                                }
                            },
                            "schema": {
                                "list": {
                                    "@type": "VariableSizeArrayElement",
                                    "fieldSize": 1,
                                    "schema": {
                                        "@type": "NullEndStringElement"
                                    }
                                },
                                "entry1": {
                                    "@type": "TypeElement",
                                    "name": "Entry"
                                },
                                "entry2": {
                                    "@type": "TypeElement",
                                    "name": "Entry"
                                }
                            }
                        }""";
        ParserTester parserTester = new ParserTester();
        parserTester.test(json, "02496E7465676572456E74727900537472696E67456E74727900000501416E6100",
                List.of(
                        Pair.of("list", new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", new ArrayData(List.of(
                                new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", 1, 13, new StringData("IntegerEntry")),
                                new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", 14, 12,  new StringData("StringEntry"))
                        )))),
                        Pair.of("entry1", new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 26, 2, new UnionData(
                                new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 26, 1, new StringData("IntegerEntry")),
                                new IntData(0, false, 1),
                                new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 27, 1, StructData.from(List.of(Pair.of("value", new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 27, 1, new IntData(5, false, 1))))))
                        ))),
                        Pair.of("entry2", new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 28, 5, new UnionData(
                                new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 28, 1, new StringData("StringEntry")),
                                new IntData(1, false, 1),
                                new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 29, 4, StructData.from(List.of(Pair.of("value", new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 29, 4, new StringData("Ana"))))))
                        )))
                )
        );
    }

    @Test
    void testArrayStringRefClassifierStruct() {
        String json = """
                        {
                            "types": {
                                "Data": {
                                    "@type": "StructType",
                                    "members": {
                                        "list": {
                                            "@type": "VariableSizeArrayElement",
                                            "fieldSize": 1,
                                            "schema": {
                                                "@type": "NullEndStringElement"
                                            }
                                        },
                                        "entry1": {
                                            "@type": "TypeElement",
                                            "name": "Entry"
                                        },
                                        "entry2": {
                                            "@type": "TypeElement",
                                            "name": "Entry"
                                        }
                                    }
                                },
                                "Entry": {
                                    "@type": "UnionType",
                                    "classifier": {
                                        "@type": "ArrayStringRefClassifier",
                                        "size": 1,
                                        "variable": "list"
                                    }
                                },
                                "IntegerEntry": {
                                    "@type": "StructType",
                                    "members": {
                                    "value": {
                                        "@type": "IntElement",
                                        "size": 1
                                        }
                                    }
                                },
                                "StringEntry": {
                                    "@type": "StructType",
                                    "members": {
                                        "value": {
                                            "@type": "NullEndStringElement"
                                        }
                                    }
                                }
                            },
                            "schema": {
                                "data": {
                                    "@type": "TypeElement",
                                    "name": "Data"
                                }
                            }
                        }""";
        ParserTester parserTester = new ParserTester();
        parserTester.test(json, "02496E7465676572456E74727900537472696E67456E74727900000501416E6100", List.of(Pair.of("data", new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", StructData.from(
                List.of(
                        Pair.of("list", new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", new ArrayData(List.of(
                                new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", 1, 13, new StringData("IntegerEntry")),
                                new ParseObject("02496e7465676572456e74727900537472696e67456e74727900", 14, 12,  new StringData("StringEntry"))
                        )))),
                        Pair.of("entry1", new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 26, 2, new UnionData(
                                new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 26, 1, new StringData("IntegerEntry")),
                                new IntData(0, false, 1),
                                new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 27, 1, StructData.from(List.of(Pair.of("value", new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 27, 1, new IntData(5, false, 1))))))
                        ))),
                        Pair.of("entry2", new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 28, 5, new UnionData(
                                new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 28, 1, new StringData("StringEntry")),
                                new IntData(1, false, 1),
                                new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 29, 4, StructData.from(List.of(Pair.of("value", new ParseObject("02496E7465676572456E74727900537472696E67456E74727900000501416E6100", 29, 4, new StringData("Ana"))))))
                        )))
                )
        )))));
    }

    @Test
    void testArrayStringRefClassifierRawZeroIsNull() {
        String json = """
                        {
                            "types": {
                                "Entry": {
                                    "@type": "UnionType",
                                    "classifier": {
                                        "@type": "ArrayStringRefClassifier",
                                        "size": 1,
                                        "variable": "list",
                                        "zeroIsNull": true
                                    }
                                },
                                "IntegerEntry": {
                                    "@type": "StructType",
                                    "members": {
                                    "value": {
                                        "@type": "IntElement",
                                        "size": 1
                                        }
                                    }
                                },
                                "StringEntry": {
                                    "@type": "StructType",
                                    "members": {
                                        "value": {
                                            "@type": "NullEndStringElement"
                                        }
                                    }
                                }
                            },
                            "schema": {
                                "list": {
                                    "@type": "VariableSizeArrayElement",
                                    "fieldSize": 1,
                                    "schema": {
                                        "@type": "NullEndStringElement"
                                    }
                                },
                                "entry1": {
                                    "@type": "TypeElement",
                                    "name": "Entry"
                                },
                                "entry2": {
                                    "@type": "TypeElement",
                                    "name": "Entry"
                                },
                                "entry3": {
                                    "@type": "TypeElement",
                                    "name": "Entry"
                                }
                            }
                        }""";
        ParserTester parserTester = new ParserTester();
        parserTester.test(json, "02496E7465676572456E74727900537472696E67456E7472790000010502416E6100",
                List.of(
                        Pair.of("list", new ParseObject("02496E7465676572456E74727900537472696E67456E74727900", new ArrayData(List.of(
                                new ParseObject("02496E7465676572456E74727900537472696E67456E74727900", 1, 13, new StringData("IntegerEntry")),
                                new ParseObject("02496E7465676572456E74727900537472696E67456E74727900", 14, 12,  new StringData("StringEntry"))
                        )))),
                        Pair.of("entry1", new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 26, 1, new UnionData(
                                new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 26, 1, new NullData()),
                                new IntData(0, false, 1),
                                new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 26, 1, new NullData())
                        ))),
                        Pair.of("entry2", new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 27, 2, new UnionData(
                                new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 27, 1, new StringData("IntegerEntry")),
                                new IntData(0, false, 1),
                                new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 28, 1, StructData.from(List.of(Pair.of("value", new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 28, 1, new IntData(5, false, 1))))))
                        ))),
                        Pair.of("entry3", new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 29, 5, new UnionData(
                                new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 29, 1, new StringData("StringEntry")),
                                new IntData(1, false, 1),
                                new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 30, 4, StructData.from(List.of(Pair.of("value", new ParseObject("02496E7465676572456E74727900537472696E67456E7472790000010502416E6100", 30, 4, new StringData("Ana"))))))
                        )))
                )
        );
    }
}