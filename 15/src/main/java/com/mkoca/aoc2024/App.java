package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class App {
    public static final String FILE_NAME = "input.txt";
    public static final int[][] DIRECTIONS = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // UP, RIGHT, DOWN, LEFT

    public static void main(String[] args) {
        part1();
        // part2();
    }

    public static void part1() {
        List<String> input = readFile();
        int[] start = new int[2];
        char[][] map = createMap(input, start);
        String moves = String.join("", input.subList(map.length + 1, input.size()));

        int[] pos = start.clone();

        for (int i = 0; i < moves.length(); i++) {
            int[] dir = getDir(moves.charAt(i));
            int[] next = new int[]{pos[0] + dir[0], pos[1] + dir[1]};

            if (isOutOfBounds(map, next[0], next[1])) continue;

            char nextC = map[next[0]][next[1]];
            if (nextC == '.') {
                pos = next;
            } else if (nextC == 'O') {
                List<Integer[]> cells = canPush(map, next, dir); // linear representation of cells with food that ends with an empty space

                if (!cells.isEmpty()) {
                    Integer[] emptyCell = cells.getLast();
                    map[emptyCell[0]][emptyCell[1]] = 'O';

                    Integer[] from = cells.getFirst();
                    map[from[0]][from[1]] = '.';

                    pos = new int[]{from[0], from[1]};
                }
            }
        }

        long res = 0L;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 'O') res += 100L * i + j;
            }
        }

        printMap(map, pos);
        System.out.println("Part 1: " + res);
    }

    public static void part2() {

    }

    public static List<Integer[]> canPush(char[][] map, int[] from, int[] dir) {
        Integer[] cur = new Integer[]{from[0], from[1]};
        List<Integer[]> linear = new ArrayList<>();
        while (!isOutOfBounds(map, cur[0], cur[1])) {
            linear.add(cur);

            if (map[cur[0]][cur[1]] == '.') {
                assert linear.size() >= 2;
                return linear;
            } else if (map[cur[0]][cur[1]] == '#') {
                Integer[] last = linear.getLast();

                if (map[last[0]][last[1]] == '.') return linear;
                else return Collections.emptyList();
            }

            cur = new Integer[]{cur[0] + dir[0], cur[1] + dir[1]};
        }

        return Collections.emptyList();
    }

    public static boolean isOutOfBounds(char[][] map, int x, int y) {
        return x < 0 || x >= map.length || y < 0 || y >= map[0].length;
    }

    public static char reverse(char c) {
        return switch (c) {
            case '^' -> 'v';
            case '>' -> '<';
            case 'v' -> '^';
            case '<' -> '>';
            default -> {
                System.out.println("ERROR");
                throw new IllegalArgumentException("Illegal direction input!");
            }
        };
    }

    public static int[] getDir(char c) {
        return switch (c) {
            case '^' -> DIRECTIONS[0];
            case '>' -> DIRECTIONS[1];
            case 'v' -> DIRECTIONS[2];
            case '<' -> DIRECTIONS[3];
            default -> {
                System.out.println("ERROR");
                throw new IllegalArgumentException("Illegal direction input!");
            }
        };
    }

    public static void printMap(char[][] map, int[] robotPos) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (i == robotPos[0] && j == robotPos[1]) {
                    System.out.print('@');
                } else {
                    System.out.print(map[i][j]);
                }
            }
            System.out.println();
        }
    }

    public static char[][] createMap(List<String> input, int[] start) {
        int ROW_SIZE = IntStream.range(0, input.size()).filter(i -> input.get(i).isEmpty()).findFirst().orElseThrow();
        int COL_SIZE = input.getFirst().length();

        char[][] map = new char[ROW_SIZE][COL_SIZE];
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                char c = input.get(i).charAt(j);
                map[i][j] = c;
                if (c == '@') {
                    start[0] = i;
                    start[1] = j;
                    map[i][j] = '.';
                }
            }
        }

        return map;
    }

    public static List<String> readFile() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);
        List<String> list = new ArrayList<>();
        try {
            assert url != null;
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
