package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

public class App {
    public static final String FILE_NAME = "sample.txt";
    public static final int[][] DIRECTIONS = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // UP, RIGHT, DOWN, LEFT

    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        List<String> input = readFile();
        int[] start = new int[2];
        char[][] map = createMap(input, start, false);
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

        // printMap(map, pos);
        System.out.println("Part 1: " + res);
    }

    public static void part2() {
        List<String> input = readFile();
        int[] start = new int[2];
        char[][] map = createMap(input, start, true);
        String moves = String.join("", input.subList(IntStream.range(0, input.size() - 1).filter(i -> input.get(i).isBlank()).findFirst().orElseThrow() + 1, input.size()));

        int[] pos = start.clone();

        System.out.println("Initial state");
        printMap(map, pos);
        System.out.println();

        for (int i = 0; i < moves.length(); i++) {

            System.out.println("Move " + moves.charAt(i) + ":");
            int[] dir = getDir(moves.charAt(i));
            int[] next = new int[]{pos[0] + dir[0], pos[1] + dir[1]};

            if (isOutOfBounds(map, next[0], next[1])) continue;

            char nextC = map[next[0]][next[1]];
            if (nextC == '.') {
                pos = next;
            } else if (nextC == '[' || nextC == ']') {
                if (dir[1] != 0) {
                    // if horizontal
                    List<Integer[]> cells = canPushHorizontal(map, next, dir); // linear representation of cells with food that ends with an empty space

                    if (!cells.isEmpty()) {
                        if (dir[1] != 0) {
                            // if horizontal

                            for (int j = cells.size() - 1; j > 0; j--) {
                                Integer[] curCell = cells.get(j);
                                Integer[] prevCell = cells.get(j - 1);

                                map[curCell[0]][curCell[1]] = map[prevCell[0]][prevCell[1]];
                            }

                            Integer[] from = cells.getFirst();
                            map[from[0]][from[1]] = '.';

                            pos = new int[]{from[0], from[1]};
                        }
                    }
                } else {
                    // if vertical
                    Map<Integer, List<Integer[]>> rows = canPushVertical(map, next, dir);

                    if (!rows.isEmpty()) {
                        // get latest row and check if all next is empty
                        int highestRow = rows.keySet().stream().min(Comparator.comparingInt(Integer::intValue)).orElseThrow();

                        boolean nextRowIsAvailable = true;

                        for (var cell : rows.get(highestRow)) {
                            int[] verticalNext = new int[]{cell[0] + dir[0], cell[1] + dir[1]};

                            if (isOutOfBounds(map, verticalNext[0], verticalNext[1])) {
                                nextRowIsAvailable = false;
                                break;
                            }

                            if (map[verticalNext[0]][verticalNext[1]] != '.') {
                                nextRowIsAvailable = false;
                                break;
                            }
                        }

                        if (nextRowIsAvailable) {
                            // move everything in rows by one position of dir
                            System.out.println("moving");
                            List<Integer> rowsToMove = rows.keySet().stream().sorted().toList();

                            for (var row : rowsToMove) {
                                for (var cell : rows.get(row)) {
                                    int[] nextVertical = new int[]{cell[0] + dir[0], cell[1] + dir[1]};
                                    map[nextVertical[0]][nextVertical[1]] = map[cell[0]][cell[1]];
                                    map[cell[0]][cell[1]] = '.';
                                }
                            }

                            pos[0] = next[0];
                            pos[1] = next[1];
                        }
                    }
                }
            }

            printMap(map, pos);
            System.out.println();
        }

        printMap(map, pos);

        long res = 0L;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == '[') res += 100L * i + j;
            }
        }

        System.out.println("Part 2: " + res);
    }

    static void add(Map<Integer, List<Integer[]>> map, Integer[] cell) {
        List<Integer[]> row = map.get(cell[0]);

        if (row != null) {
            row.add(cell);
        } else {
            row = new ArrayList<>();
            row.add(cell);
            map.put(cell[0], row);
        }
    }

    public static Map<Integer, List<Integer[]>> canPushVertical(char[][] map, int[] from, int[] dir) {
        Integer[] cur = new Integer[]{from[0], from[1]};

        Map<Integer, List<Integer[]>> rows = new HashMap<>();
        boolean[][] visited = new boolean[map.length][map[0].length];

        Queue<Integer[]> queue = new ArrayDeque<>();
        queue.add(cur);

        while (!queue.isEmpty()) {
            cur = queue.poll();

            if (isOutOfBounds(map, cur[0], cur[1]) || visited[cur[0]][cur[1]]) continue;

            if (map[cur[0]][cur[1]] == '#') {
                System.out.println("VERTICAL CANT");
                rows = Collections.emptyMap();
                break;
            }

            if (map[cur[0]][cur[1]] == '.') {
                visited[cur[0]][cur[1]] = true;
                continue;
            }

            visited[cur[0]][cur[1]] = true;
            add(rows, cur);

            // expand horizontally
            if (map[cur[0]][cur[1]] == '[') {
                Integer[] right = new Integer[]{cur[0], cur[1] + 1};
                if (!isOutOfBounds(map, right[0], right[1]) && !visited[right[0]][right[1]]) queue.add(right);
            } else {
                Integer[] left = new Integer[]{cur[0], cur[1] - 1};
                if (!isOutOfBounds(map, left[0], left[1]) && !visited[left[0]][left[1]]) queue.add(left);
            }

            // add neighbours in dir
            Integer[] next = new Integer[]{cur[0] + dir[0], cur[1] + dir[1]};
            if (!isOutOfBounds(map, next[0], next[1]) && !visited[next[0]][next[1]]) {
                queue.add(next);
            }
        }

        System.out.println("ROW SIZE: " + (Integer) rows.values().stream().map(List::size).mapToInt(Integer::intValue).sum());
        return rows;
    }

    public static List<Integer[]> canPushHorizontal(char[][] map, int[] from, int[] dir) {
        Integer[] cur = new Integer[]{from[0], from[1]};
        List<Integer[]> cellsToMove = new ArrayList<>();

        while (!isOutOfBounds(map, cur[0], cur[1])) {
            cellsToMove.add(cur);

            if (map[cur[0]][cur[1]] == '.') {
                assert cellsToMove.size() >= 3;
                return cellsToMove;
            } else if (map[cur[0]][cur[1]] == '#') {
                Integer[] last = cellsToMove.getLast();

                if (map[last[0]][last[1]] == '.') return cellsToMove;
                else return Collections.emptyList();
            }

            cur = new Integer[]{cur[0] + dir[0], cur[1] + dir[1]};
        }
        return Collections.emptyList();
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

    public static char[][] createMap(List<String> input, int[] start, boolean doubleIt) {
        int ROW_SIZE = IntStream.range(0, input.size()).filter(i -> input.get(i).isEmpty()).findFirst().orElseThrow();
        int COL_SIZE = input.getFirst().length();

        char[][] map = new char[ROW_SIZE][COL_SIZE * (doubleIt ? 2 : 1)];
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                char c = input.get(i).charAt(j);
                if (doubleIt) {
                    int newJ = j * 2;
                    if (c == 'O') {
                        map[i][newJ] = '[';
                        map[i][newJ + 1] = ']';
                    } else {
                        if (c == '@') {
                            map[i][newJ] = '.';
                            map[i][newJ + 1] = '.';
                            start[0] = i;
                            start[1] = newJ;
                        } else {
                            map[i][newJ] = c;
                            map[i][newJ + 1] = c;
                        }
                    }


                } else {
                    map[i][j] = c;
                    if (c == '@') {
                        start[0] = i;
                        start[1] = j;
                        map[i][j] = '.';
                    }
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
