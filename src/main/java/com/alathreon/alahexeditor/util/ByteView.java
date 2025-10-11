package com.alathreon.alahexeditor.util;

import com.alathreon.alahexeditor.parsing.Endianness;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ByteView implements Iterable<Byte> {
    public static byte[] parseFormattedString(String s) throws IllegalArgumentException{
        int[] array = s.lines().filter(l -> l.matches("[\\da-f]{8} {2}(([\\da-f]{2})  ?){0,16} {0,49} \\|.{0,16}\\|"))
                .map(l -> l.replaceAll("^[\\da-f]{8} *", "").replaceAll(" *\\|.{0,16}\\|$", ""))
                .flatMap(l -> Stream.of(l.split(" +")))
                .mapToInt(c -> Integer.parseInt(c, 16))
                .toArray();
        if(array.length == 0) {
            throw new IllegalArgumentException("Nothing to parse in:%n%s".formatted(s));
        }
        byte[] bytes = new byte[array.length];
        for(int i = 0; i < array.length; i++) {
            bytes[i] = (byte) array[i];
        }
        return bytes;
    }
    public static ByteView fromFormattedString(String s) throws IllegalArgumentException {
        return  new ByteView(parseFormattedString(s));
    }

    private final byte[] content;
    private final int offset;
    private final int length;

    public ByteView(byte[] content) {
        this(content, 0, content.length);
    }
    public ByteView(byte[] content, int offset, int length) {
        if(offset < 0 || length < 0 || offset + length > content.length) throw new IllegalArgumentException();
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
    public int capacity() {
        return content.length;
    }
    public byte[] content() {
        return content;
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
    public int neededToFit(int i, String s) {
        if(s.length() % 2 == 1 || !s.matches("^[0-9A-F]*$")) throw new IllegalArgumentException("Invalid data " + s);
        return Math.max((i + s.length() / 2) - length, 0);
    }
    public void set(int i, String s) {
        if(i + s.length() / 2 > length || s.length() % 2 == 1 || !s.matches("^[0-9A-F]*$")) throw new IllegalArgumentException("For ByteView of length %d, with i=%d and s=%s".formatted(length, i, s));
        for(int j = 0; j < s.length() / 2; j++) {
            content[offset + i] = (byte)Integer.parseInt(s.substring(j * 2, j * 2 + 2), 16);
        }
    }
    public ByteView withIncreasedLength(int lengthToAdd) {
        if(offset + length + lengthToAdd <= capacity()) {
            return new ByteView(content, offset, length + lengthToAdd);
        }
        int newCapacity = Math.max(offset + length + lengthToAdd, capacity() * 2);
        byte[] newBytes = Arrays.copyOfRange(content, 0, newCapacity);
        return new ByteView(newBytes, offset, length + lengthToAdd);
    }
    public ByteView subView(int length) {
        return subView(0, length);
    }
    public ByteView subView(int offset, int length) {
        if(offset < 0 || length < 0 || offset + length > this.length) throw new IllegalArgumentException();
        return new ByteView(content, this.offset + offset, length);
    }
    public ByteView takeWhile(Predicate<Byte> predicate) {
        int i = 0;
        for(; i < length; i++) {
            if(!predicate.test(get(i))) {
                break;
            }
        }
        return subView(i);
    }

    public ByteView leftover(ByteView subView) {
        if(subView.content != this.content) throw new IllegalArgumentException();
        return subView(this.offset + subView.offset + subView.length, length - (subView.offset + subView.length));
    }

    public Stream<Byte> stream() {
        return IntStream.range(offset, offset + length).mapToObj(i -> content[i]);
    }
    public Stream<Byte> streamReversed() {
        return IntStream.range(offset, offset + length).mapToObj(i -> content[length - 1 - i]);
    }
    @Override
    public Iterator<Byte> iterator() {
        return stream().iterator();
    }
    public Iterator<Byte> iteratorReversed() {
        return streamReversed().iterator();
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
    public String toFormmatedString() {
        StringBuilder sb = new StringBuilder();
        for(int row = 0; row * 16 < length + 16; row++) {
            sb.append(String.format("%08X", row * 16));
            int size = Math.min(offset + length, offset + row * 16 + 16);
            StringBuilder rowBuilder = new StringBuilder();
            int col = 0;
            for(int i = offset + row * 16; i < size; i++) {
                rowBuilder.append(String.format("%02X ", content[i]));
                if(col % 8 == 7) {
                    rowBuilder.append(' ');
                }
                col++;
            }
            sb.append("%-52s|".formatted(rowBuilder.toString()));
            for(int i = offset + row * 16; i < size; i++) {
                char c = (char)(content[i] & 0xFF);
                if(c <= 32) {
                    c = '.';
                }
                sb.append(c);
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

    public byte[] getBytes() {
        byte[] result = new byte[length];
        System.arraycopy(content, offset, result, 0, length);
        return result;
    }

    public DataSegment toDataSegment() {
        return new DataSegment(offset, length, toString());
    }
    public BitSet toBitset() {
        return BitSet.valueOf(Arrays.copyOfRange(content, offset, offset + length));
    }

    public long parseInt(Endianness endianness) {
        long result = 0;
        for(int i = 0; i < length; i++) {
            byte b = get(i);
            result |= (long) (b & 0xFF) << ((endianness == Endianness.BIG ? length - 1 - i : i) * 8);
        }
        return result;
    }
}
