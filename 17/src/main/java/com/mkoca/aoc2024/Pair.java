package com.mkoca.aoc2024;

public class Pair<T> {

    public T first;
    public T second;

    public Pair(T a, T b) {
        this.first = a;
        this.second = b;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first, second);
    }
}
