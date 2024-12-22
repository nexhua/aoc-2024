package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class App {
    public static final String FILE_NAME = "input.txt";
    public static final int[][] DIRECTIONS = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // UP, RIGHT, DOWN, LEFT

    public static void main(String[] args) {
        part1();
        // part2();
    }

    public static void part1() {
        Cell[][] grid = createGrid(readFile());
        List<Group> groups = createGroups(grid);

        int res = 0;
        for (var group : groups) {
            res += calculatePrice(grid, group);
        }

        // printGrid(grid);
        System.out.println("Part 1: " + res);
    }

    public static void part2() {

    }

    public static int calculatePrice(Cell[][] grid, Group group) {
        int perimeter = 0;
        for (var c : group.cells) {
            List<Cell> neighbours = getNeighbours(grid, c);
            int outbound = 4 - neighbours.size();
            int other = (int) neighbours.stream().filter(n -> n.c != c.c).count();
            perimeter += outbound + other;
        }
        return perimeter * group.cells.size();
    }

    public static List<Group> createGroups(Cell[][] grid) {
        List<Group> groups = new ArrayList<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Cell cell = grid[i][j];

                if (!cell.isGrouped) {
                    Queue<Cell> queue = new ArrayDeque<>();
                    queue.add(cell);

                    Group group = new Group(cell.c);
                    group.add(cell);


                    while (!queue.isEmpty()) {
                        Cell c = queue.poll();

                        getNeighboursOpt(grid, c).forEach(n -> {
                            group.add(n);
                            queue.add(n);
                        });

                    }
                    groups.add(group);
                }
            }
        }

        return groups;
    }

    public static List<Cell> getNeighbours(Cell[][] grid, Cell cell) {
        List<Cell> neigh = new ArrayList<>();
        for (var dir : DIRECTIONS) {
            int x = cell.x + dir[0];
            int y = cell.y + dir[1];
            if ((0 <= x && x < grid.length) && (0 <= y && y < grid[0].length)) neigh.add(grid[x][y]);
        }
        return neigh;
    }

    public static List<Cell> getNeighboursOpt(Cell[][] grid, Cell cell) {
        List<Cell> neigh = new ArrayList<>();
        for (var dir : DIRECTIONS) {
            int x = cell.x + dir[0];
            int y = cell.y + dir[1];
            if ((0 <= x && x < grid.length) && (0 <= y && y < grid[0].length) && !grid[x][y].isGrouped && grid[x][y].c == cell.c)
                neigh.add(grid[x][y]);
        }
        return neigh;
    }

    public static void printGrid(Cell[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                System.out.printf("%c", grid[i][j].c);
            }
            System.out.println();
        }
    }

    public static Cell[][] createGrid(List<String> input) {
        Cell[][] grid = new Cell[input.size()][input.getFirst().length()];
        for (int i = 0; i < input.size(); i++) {
            String row = input.get(i);
            for (int j = 0; j < input.get(i).length(); j++) {
                grid[i][j] = new Cell(i, j, row.charAt(j));
            }
        }
        return grid;
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
