package com.mkoca.aoc2024;

import java.util.Objects;

public class Cell {
    public int x;
    public int y;
    public char c;
    public boolean isGrouped;

    public Cell(int x, int y, char c) {
        this.x = x;
        this.y = y;
        this.c = c;
        this.isGrouped = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return x == cell.x && y == cell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("{%d, %d}", x, y);
    }
}
