package com.mkoca.aoc2024;

public class Cell {
    public int x;
    public int y;
    public char c;
    public boolean isAnti = false;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.c = '.';
    }

    public Cell(int x, int y, char c) {
        this.x = x;
        this.y = y;
        this.c = c;
    }

    public Cell min(Cell cell) {
        if (this.x + this.y < cell.x + cell.y) return this;
        return cell;
    }
}
