package com.alathreon.alahexeditor.parsing.template.type.union;

import com.alathreon.alahexeditor.parsing.object.Data;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.util.ByteView;

public record ClassifierResult(String structName, Data data, IntData intData, ByteView segment, ByteView leftover) {
}
