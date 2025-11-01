package com.alathreon.alahexeditor.util;

public record DataSegment(int offset, int length, String hex) {
    public byte[] toByteArray() {
        byte[] array = new byte[hex.length()/2];
        for(int i = 0; i < hex.length(); i += 2) {
            array[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
        }
        return array;
    }
    public ByteView toByteView() {
        return new ByteView(toByteArray());
    }
    public String toUtf8() {
        return new String(toByteArray());
    }
}
