package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class App {
    public static final String FILE_NAME = "input.txt";
    private static final int[][] DIRECTIONS = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // UP, RIGHT, DOWN, LEFT

    public static void main(String[] args) {
        part1();
    }

    public static void part1() {
        List<String> input = readFile();
        Cell[][] grid = new Cell[input.size()][input.get(0).length()];

        for (int i = 0; i < grid.length; i++) {
            String line = input.get(i);
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = new Cell(i, j, Character.digit(line.charAt(j), 10));
            }
        }

        bfs(grid, grid[0][2]);
        Map<Cell, Set<Cell>> trails = new HashMap<>();

        for (var row : grid) {
            for (var cell : row) {
                if (cell.val == 0) {
                    trails.put(cell, bfs(grid, cell));
                }
            }
        }

        long res = 0;
        for (var set : trails.values()) {
            res += set.size();
        }

        // printGrid(grid);
        System.out.println("Part 1: " + res);
    }

    public static Set<Cell> bfs(Cell[][] grid, Cell start) {
        Queue<Cell> queue = new ArrayDeque<>();
        queue.add(start);

        Set<Cell> reached = new HashSet<>();

        while (!queue.isEmpty()) {
            Cell c = queue.poll();
            if (c.val == 9) reached.add(c);
            if (c.val < 10) getNeighbours(grid, c).stream().filter(n -> n.val - c.val == 1).forEach(queue::add);
        }

        return reached;
    }

    public static List<Cell> getNeighbours(Cell[][] grid, Cell cell) {
        List<Cell> neighbours = new ArrayList<>();
        for (var direction : DIRECTIONS) {
            int x = cell.x + direction[0];
            int y = cell.y + direction[1];
            if ((0 <= x && x < grid.length) && (0 <= y && y < grid[0].length)) {
                neighbours.add(grid[x][y]);
            }
        }

        return neighbours;
    }

    public static void printGrid(Cell[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                System.out.printf("%2d", grid[i][j].val);
            }
            System.out.println();
        }
    }

    public static List<String> readFile() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);
        List<String> list = new ArrayList<>();
        try {
            File file = new File(url.getPath());
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = bf.readLine();

            while (line != null) {
                list.add(line);
                line = bf.readLine();
            }
        } catch (NullPointerException | IOException e) {
            System.exit(1);
        }

        return list;
    }
}


