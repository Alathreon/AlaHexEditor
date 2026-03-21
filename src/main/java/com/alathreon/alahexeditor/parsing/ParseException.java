package com.alathreon.alahexeditor.parsing;

import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.util.ByteView;

public class ParseException extends Exception {
    public static class PartialParseException extends Exception {
        public PartialParseException(String message) {
            super(message);
        }

        public ParseException complete(ByteView causeData, ParseObjects objects) {
            return new ParseException(causeData, objects, getMessage());
        }
    }

    public static PartialParseException partial(String message) {
        return new PartialParseException(message);
    }

    private static String formatSegment(ByteView seg) {
        if(seg.length() <= 10) {
            return "[" + seg.offset() + ":" + seg + "]";
        } else {
            return "[" + seg.offset() + "+" + seg.length() + "]";
        }
    }

    private final ByteView causeData;
    private final ParseObjects debugData;

    public ParseException(ByteView view, ParseObjects debugData, String message) {
        super(formatSegment(view) + " " + message);
        this.causeData = view;
        this.debugData = debugData;
    }

    public ByteView getCauseData() {
        return causeData;
    }

    public ParseObjects getDebugData() {
        return debugData;
    }

}
