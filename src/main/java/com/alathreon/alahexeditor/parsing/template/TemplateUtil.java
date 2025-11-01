package com.alathreon.alahexeditor.parsing.template;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.util.ByteView;

public class TemplateUtil {
    private TemplateUtil() {}
    public static boolean isPowerOfTwo(int n) {
        return n > 0 && ((n & (n - 1)) == 0);
    }
    public static boolean isWithinMaxByteSize(int actualSize, int maxSize) {
        return actualSize <= maxSize && isPowerOfTwo(actualSize);
    }
    public static void ensureMaxByteSize(int actualSize, int maxSize) {
        if(!isWithinMaxByteSize(actualSize, maxSize)) {
            throw new IllegalArgumentException(actualSize + " is larger than " + maxSize + " or isn't a power of two");
        }
    }

    public static ByteView safeSubView(ByteView data, int length) throws ParseException {
        if(length > data.length()) {
            throw new ParseException(data, length + " > " + data.length());
        }
        return data.subView(length);
    }
    public static <T extends SchemaType<?>> T findType(Template template, ByteView data, String name, Class<T> clazz) throws ParseException {
        return template.findType(name, clazz, () -> new ParseException(data, "%s %s not found".formatted(clazz.getSimpleName(), name)));
    }
    public static SchemaType<?> findType(Template template, ByteView data, String name) throws ParseException {
        return template.findType(name, () -> new ParseException(data, "Type %s not found".formatted(name)));
    }
}
