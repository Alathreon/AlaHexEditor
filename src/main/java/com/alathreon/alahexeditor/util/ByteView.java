package com.alathreon.alahexeditor.util;

import com.alathreon.alahexeditor.parsing.Endianness;

import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ByteView implements Iterable<Byte> {
    public static byte[] parseFormattedString(String s) throws IllegalArgumentException{
        int[] array = s.lines().filter(l -> l.matches("[\\da-fA-F]{8} {2}(([\\da-fA-F]{2})  ?){0,16} {0,49} \\|.{0,16}\\|"))
                .map(l -> l.replaceAll("^[\\da-fA-F]{8} *", "").replaceAll(" *\\|.{0,16}\\|$", ""))
                .flatMap(l -> Stream.of(l.split(" +")))
                .mapToInt(c -> Integer.parseInt(c, 16))
                .toArray();
        if(array.length == 0) {
            return s.getBytes();
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
    public static ByteView fromHexString(String hex) throws IllegalArgumentException {
        if(hex.length() % 2 != 0) throw new IllegalArgumentException();
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return new ByteView(bytes);
    }
    public static ByteView fromStream(Stream<Byte> stream) {
        return fromList(stream.toList());
    }
    public static ByteView fromList(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for(int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return new ByteView(bytes);
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
    public boolean isIn(int i) {
        return i < length;
    }
    public boolean isIn(Position position) {
        return isIn(Position.positionToIndex(position));
    }
    public byte get(int i) {
        if(!isIn(i)) throw new IllegalArgumentException();
        return content[offset + i];
    }
    public byte get(Position pos) {
        return get(Position.positionToIndex(pos));
    }
    public void set(int i, byte val) {
        if(!isIn(i)) throw new IllegalArgumentException();
        content[offset + i] = val;
    }
    public void set(Position pos, byte val) {
        set(Position.positionToIndex(pos), val);
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
    public ByteView subViewOffset(int offset) {
        return subView(offset, length - offset);
    }
    public ByteView subView(int offset, int length) {
        if(offset < 0 || length < 0 || offset + length > this.length) throw new IllegalArgumentException("offset=%d, length=%d".formatted(offset, length));
        return new ByteView(content, this.offset + offset, length);
    }
    public ByteView takeWhile(Predicate<Byte> predicate, boolean inclusive) {
        int i = 0;
        for(; i < length; i++) {
            if(!predicate.test(get(i))) {
                break;
            }
        }
        return subView(i + (i < length && inclusive ? 1 : 0));
    }
    public ByteView withAll(List<Position> positions) {
        byte[] result = new byte[positions.size()];
        for(int i = 0; i < result.length; i++) {
            result[i] = get(positions.get(i));
        }
        return new ByteView(result);
    }
    public ByteView withoutAll(Set<Position> positions) {
        byte[] result = new byte[length];
        int len = 0;
        for(int i = 0; i < length; i++) {
            Position position = Position.indexToPosition(i);
            if(!positions.contains(position)) {
                result[len++] = content[offset + i];
            }
        }
        return new ByteView(Arrays.copyOf(result, len));
    }

    public ByteView withInsert(ByteView toInsert, Position position) {
        int start = Position.positionToIndex(position);
        if(start >= length) {   // if position > this.length
            byte[] result = new byte[start + toInsert.length]; // [this, other]
            System.arraycopy(content, offset, result, 0, length); // This
            System.arraycopy(toInsert.content, toInsert.offset, result, start, toInsert.length);    // Other
            return new ByteView(result);
        }
        byte[] result = new byte[length + toInsert.length]; // [this1, other, this2]
        System.arraycopy(content, offset, result, 0, start);    // This1
        System.arraycopy(toInsert.content, toInsert.offset, result, start, toInsert.length);    // Other
        System.arraycopy(content, offset + start, result, start + toInsert.length, length - start);   // This2
        return new ByteView(result);
    }

    public ByteView leftover(ByteView subView) {
        if(subView.content != this.content) throw new IllegalArgumentException();
        if(subView.offset < this.offset) throw new IllegalArgumentException();
        if(subView.offset + subView.length > this.offset + this.length) throw new IllegalArgumentException();
        int start = subView.offset + subView.length - this.offset;
        int end =  this.length - start;
        return subView(start, end);
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
    public String toBinaryString() {
        return stream().map(b -> String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0')).collect(Collectors.joining(""));
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
    public String toTextString(Charset charset, boolean stopAtNull) {
        String s = new String(content, offset, length, charset);
        if(stopAtNull) {
            int idx = s.indexOf('\0');
            if(idx != -1) {
                s = s.substring(0, idx);
            }
        }
        return s;
    }
    public String toFormmatedString() {
        StringBuilder sb = new StringBuilder();
        for(int row = 0; row * 16 < length; row++) {    // row += 16 instead of row*16
            sb.append(String.format("%08X  ", row * 16));
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
