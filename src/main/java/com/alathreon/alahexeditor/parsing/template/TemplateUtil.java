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
    public static void ensureNonNegative(int value) {
        if(value < 0) {
            throw new IllegalArgumentException(value + " is negative");
        }
    }
    public static void ensurePositive(int value) {
        if(value <= 0) {
            throw new IllegalArgumentException(value + " is negative or zero");
        }
    }

    public static ByteView safeSubView(ByteView data, int length) throws ParseException {
        if(length > data.length()) {
            throw new ParseException(data, length + " > " + data.length());
        }
        return data.subView(length);
    }
    public static ByteView safeSubView(ByteView data, int offset, int length) throws ParseException {
        if(offset + length > data.length()) {
            throw new ParseException(data, (offset + length) + " > " + data.length());
        }
        return data.subView(offset, length);
    }
    public static <T extends SchemaType<?>> T findType(Template template, ByteView data, String name, Class<T> clazz) throws ParseException {
        return template.findType(name, clazz, () -> new ParseException(data, "%s %s not found".formatted(clazz.getSimpleName(), name)));
    }
    public static SchemaType<?> findType(Template template, ByteView data, String name) throws ParseException {
        return template.findType(name, () -> new ParseException(data, "Type %s not found".formatted(name)));
    }
}
