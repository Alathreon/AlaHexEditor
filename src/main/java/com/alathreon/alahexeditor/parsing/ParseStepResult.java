package com.alathreon.alahexeditor.parsing;

import com.alathreon.alahexeditor.parsing.object.Data;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.util.ByteView;

public record ParseStepResult(ByteView leftover, ParseObject object) {
    public ParseStepResult(ByteView parent, ByteView segment, Data data) {
        this(parent.leftover(segment), new ParseObject(segment.toDataSegment(), data));
    }
}
