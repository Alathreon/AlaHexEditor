package com.alathreon.alahexeditor.parsing.template.element;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.ParseStepResult;
import com.alathreon.alahexeditor.parsing.object.FloatData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaElement;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.ensureMaxByteSize;
import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.safeSubView;

public record FloatElement(int size) implements SchemaElement {
    public static float shortBitsToFloat(short bits) {
        int s = (bits >> 15) & 0x00000001; // sign
        int e = (bits >> 10) & 0x0000001F; // exponent
        int f = bits & 0x000003FF;         // fraction

        if (e == 0) {
            if (f == 0) {
                // Zero
                return Float.intBitsToFloat(s << 31);
            } else {
                // Subnormal
                while ((f & 0x00000400) == 0) {
                    f <<= 1;
                    e -= 1;
                }
                e += 1;
                f &= ~0x00000400;
            }
        } else if (e == 31) {
            // NaN or Inf
            if (f == 0) {
                return Float.intBitsToFloat((s << 31) | 0x7F800000);
            } else {
                return Float.intBitsToFloat((s << 31) | 0x7F800000 | (f << 13));
            }
        }

        e = e + (127 - 15);
        f = f << 13;

        int result = (s << 31) | (e << 23) | f;
        return Float.intBitsToFloat(result);
    }
    public static float byteBitsToFloat(byte b) {
        int bits = b & 0xFF;

        int sign = (bits >> 7) & 0x1;
        int exponent = (bits >> 3) & 0xF;
        int mantissa = bits & 0x7;

        if (exponent == 0) {
            // Subnormal or zero
            if (mantissa == 0) {
                return sign == 1 ? -0f : 0f;
            }
            // Subnormal number
            float value = (float) (mantissa / 8.0) * (float) Math.pow(2, -6);
            return sign == 1 ? -value : value;
        } else if (exponent == 0xF) {
            // Inf or NaN
            if (mantissa == 0) {
                return sign == 1 ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
            } else {
                return Float.NaN;
            }
        }

        // Normalized number
        int exp = exponent - 7; // bias = 7 for E4
        float frac = 1 + mantissa / 8.0f; // 1.xxx
        float value = (float) (frac * Math.pow(2, exp));
        return sign == 1 ? -value : value;
    }

    public FloatElement {
        ensureMaxByteSize(size, 8);
    }

    @Override
    public ParseStepResult parse(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, size);
        long bits = view.parseInt(template.endianness());
        double value = switch (size) {
            case 1 -> byteBitsToFloat((byte) bits);
            case 2 -> shortBitsToFloat((short) bits);
            case 4 -> Float.intBitsToFloat((int) bits);
            case 8 -> Double.longBitsToDouble(bits);
            default ->  throw new IllegalArgumentException();
        };
        return new ParseStepResult(data, view, new FloatData(value, size));
    }
}
