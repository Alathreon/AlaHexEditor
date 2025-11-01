package com.alathreon.alahexeditor.parsing;

import com.alathreon.alahexeditor.util.ByteView;
import com.alathreon.alahexeditor.util.DataSegment;

public class ParseException extends Exception {

    public static class PartialParseException extends Exception {
        public PartialParseException(String message) {
            super(message);
        }

        public ParseException complete(ByteView view) {
            return complete(view.toDataSegment());
        }
        public ParseException complete(DataSegment causeData) {
            return new ParseException(causeData, getMessage());
        }
    }

    public static PartialParseException partial(String message) {
        return new PartialParseException(message);
    }

    private static String formatSegment(DataSegment seg) {
        if(seg.length() <= 10) {
            return "[" + seg.offset() + ":" + seg.hex() + "]";
        } else {
            return "[" + seg.offset() + "+" + seg.length() + "]";
        }
    }

    private final DataSegment causeData;

    public ParseException(ByteView view, String message) {
        this(view.toDataSegment(), message);
    }
    public ParseException(DataSegment causeData, String message) {
        super(formatSegment(causeData) + " " + message);
        this.causeData = causeData;
    }

    public DataSegment getCauseData() {
        return causeData;
    }
}
