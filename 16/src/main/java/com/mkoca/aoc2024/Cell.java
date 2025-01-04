package com.mkoca.aoc2024;

import java.util.Objects;

public class Cell {
    int x;
    int y;
    boolean visited;
    Cell from;
    int withDir;
    long cost;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;

        this.visited = false;
        from = null;
        withDir = -1;
        cost = Long.MAX_VALUE;
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
        return String.format("(%2d,%2d)", x, y);
    }

    //region Getters

    public boolean isNotVisited() {
        return !visited;
    }

    //endregion
}
