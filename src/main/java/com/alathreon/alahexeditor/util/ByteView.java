package com.alathreon.alahexeditor.util;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ByteView implements Iterable<Byte> {
    private final byte[] content;
    private final int offset;
    private final int length;

    public ByteView(byte[] content) {
        this(content, 0, content.length);
    }
    public ByteView(byte[] content, int offset, int length) {
        if(offset < 0 || length <= 0 || offset + length > content.length) throw new IllegalArgumentException();
        this.content = content;
        this.offset = offset;
        this.length = length;
    }

    public int offset() {
        return offset;
    }

    public int length() {
        return length;
    }
    public byte get(int i) {
        if(i >= length) throw new IllegalArgumentException();
        return content[offset + i];
    }
    public void set(int i, byte val) {
        if(i >= length) throw new IllegalArgumentException();
        content[offset + i] = val;
    }
    public void set(String s) {
        set(0, s);
    }
    public void set(int i, String s) {
        if(i + s.length() / 2 > length || s.length() % 2 == 1 || !s.matches("^[0-9A-F]*$")) throw new IllegalArgumentException("For ByteView of length %d, with i=%d and s=%s".formatted(length, i, s));
        for(int j = 0; j < s.length() / 2; j++) {
            content[offset + i] = (byte)Integer.parseInt(s.substring(j * 2, j * 2 + 2), 16);
        }
    }
    public ByteView subView(int offset, int length) {
        if(offset < 0 || length <= 0 || offset + length > this.length) throw new IllegalArgumentException();
        return new ByteView(content, this.offset + offset, length);
    }

    public Stream<Byte> stream() {
        return IntStream.range(offset, offset + length).mapToObj(i -> content[i]);
    }
    @Override
    public Iterator<Byte> iterator() {
        return stream().iterator();
    }

    @Override
    public String toString() {
        return stream().map(b -> String.format("%02X", b)).collect(Collectors.joining(" "));
    }
    public String toUTF8String() {
        StringBuilder sb = new StringBuilder();
        for(int i = offset; i < offset + length; i++) {
            char c = (char)(content[i] & 0xFF);
            if(c <= 32) {
                c = '.';
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public byte[] getBytes() {
        byte[] result = new byte[length];
        System.arraycopy(content, offset, result, 0, length);
        return result;
    }
}
