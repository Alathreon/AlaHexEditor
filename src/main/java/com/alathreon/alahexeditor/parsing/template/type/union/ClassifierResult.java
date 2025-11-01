package com.alathreon.alahexeditor.parsing.template.type.union;

import com.alathreon.alahexeditor.parsing.object.Data;
import com.alathreon.alahexeditor.util.ByteView;

public record ClassifierResult(String structName, Data data, int intData, ByteView segment, ByteView leftover) {
}
