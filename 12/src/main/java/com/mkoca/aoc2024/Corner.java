package com.mkoca.aoc2024;

import java.util.Objects;

public class Corner {
    public int x;
    public int y;
    public int dir;

    public Corner(Cell c, int dir) {
        this.x = c.x;
        this.y = c.y;
        this.dir = dir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Corner corner = (Corner) o;
        return x == corner.x && y == corner.y && Objects.equals(dir, corner.dir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, dir);
    }

    @Override
    public String toString() {
        return String.format("{%d,%d,%s}", x, y, dir);
    }
}
