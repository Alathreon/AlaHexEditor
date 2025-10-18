package com.alathreon.alahexeditor.util;

public record Position(int row, int col) {
    public static Position indexToPosition(int index) {
        return new Position(index / 16, index % 16);
    }
    public static int positionToIndex(Position position) {
        return position.row() * 16 + position.col();
    }
}
