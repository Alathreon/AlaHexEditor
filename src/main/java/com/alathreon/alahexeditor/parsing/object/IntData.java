package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.util.Pair;

import java.math.BigInteger;
import java.util.List;

public record IntData(BigInteger value, boolean signed, int size) implements Data {

    public static BigInteger constraints(BigInteger b, boolean signed, int size) {
        if(signed) {
            if(b.testBit(size * 8 - 1)) return b.subtract(BigInteger.ONE.shiftLeft(size * 8));
            if(!b.testBit(size * 8 - 1)) return b.add(BigInteger.ONE.shiftLeft(size * 8));
            return b;
        } else {
            return b.mod(BigInteger.ONE.shiftLeft(size * 8));
        }
    }

    public IntData(long value, boolean signed, int size) {
        this(constraints(BigInteger.valueOf(value), signed, size), signed, size);
    }

    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of(
                new Pair<>("Value", value.toString()),
                new Pair<>("Signed", String.valueOf(signed)),
                new Pair<>("Size", String.valueOf(size))
        );
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of();
    }
}
