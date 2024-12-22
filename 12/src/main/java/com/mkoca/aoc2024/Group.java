package com.mkoca.aoc2024;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Group {

    Character type;
    Set<Cell> cells;

    public Group(char c) {
        this.type = c;
        this.cells = new HashSet<>();
    }

    public void add(Cell cell) {
        assert (cell.c == this.type);

        this.cells.add(cell);
        cell.isGrouped = true;
    }

    @Override
    public String toString() {
        return cells.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]"));
    }
}
