package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.util.DataSegment;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record ParseObject(DataSegment metadata, @JsonUnwrapped Data data) {
    public ParseObject(String hex, Data data) {
        this(ByteView.fromHexString(hex), data);
    }
    public ParseObject(ByteView metadata, Data data) {
        this(metadata.toDataSegment(), data);
    }

    public ParseObject resolveReferences() {
        ParseObject parseObject = this;
        while(parseObject.data() instanceof ArrayRefData arrayRefData) {
            parseObject = arrayRefData.data();
        }
        return parseObject;
    }
}
