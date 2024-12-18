package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static final String FILE_NAME = "input.txt";

    public static void main(String[] args) {
        part1();
    }

    public static void part1() {
        List<String> input = readFile();
        Cell[][] grid = new Cell[input.size()][input.get(0).length()];
        Map<Character, List<Cell>> antennaMap = new HashMap<>();

        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                if (c == '.') {
                    grid[i][j] = new Cell(i, j);
                } else {
                    Cell cell = new Cell(i, j, c);
                    grid[i][j] = cell;
                    upsert(antennaMap, cell);
                }
            }
        }

        for (var antennas : antennaMap.values()) {
            createAnti(antennas, grid);
        }

        int res = 0;
        for (var row : grid) {
            for (var cell : row) {
                if (cell.isAnti) res++;
            }
        }

        // dumpGrid(grid);
        System.out.println("Part 1: " + res);
    }


    public static void createAnti(List<Cell> antennas, Cell[][] grid) {
        for (int i = 0; i < antennas.size(); i++) {
            for (int j = 0; j < antennas.size(); j++) {
                if (i != j) {
                    Cell a = antennas.get(i);
                    Cell b = antennas.get(j);

                    int x = a.x - (b.x - a.x);
                    int y = a.y - (b.y - a.y);

                    if ((x >= 0 && x < grid.length) && (y >= 0 && y < grid[0].length)) {
                        Cell cell = grid[x][y];
                        cell.isAnti = true;
                    }
                }
            }
        }
    }

    public static void upsert(Map<Character, List<Cell>> map, Cell cell) {
        List<Cell> cells = map.get(cell.c);
        if (cells != null) {
            cells.add(cell);
        } else {
            map.put(cell.c, new ArrayList<>(List.of(cell)));
        }
    }

    public static void dumpGrid(Cell[][] grid) {
        for (var row : grid) {
            for (var cell : row) {
                System.out.print(cell.isAnti ? '#' : cell.c);
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

