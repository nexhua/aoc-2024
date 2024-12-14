package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class App {
    public static final String FILE_NAME = "input.txt";

    public static Set<String> VISITED = new HashSet<>();
    public static int[][] MAP;


    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        List<String> list = readFile();
        int[][] map = new int[list.size()][list.get(0).length()];
        int[] pos = {0, 0};
        int dir = 0;

        initMap(map, pos, list);
        runPaths(map, pos, dir);

        int res = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 1) {
                    res++;
                }
            }
        }

        MAP = deepCopy(map);


        // printMap(map);
        System.out.println("Part 1: " + res);
    }

    public static void part2() {
        List<String> list = readFile();
        int[][] map = new int[list.size()][list.get(0).length()];
        int[] pos = {0, 0};
        int dir = 0;
        Set<String> resultSet = new HashSet<>();
        Set<String> visited = new HashSet<>();

        initMap(map, pos, list);
        runPathsPart2(resultSet, map, pos, dir, visited);

        System.out.println("Results");
        for (var resultPos : resultSet) {
            System.out.println(resultPos);
        }

        // printMap(map);
        System.out.println("Part 2: " + resultSet.size());
    }

    public static void runPaths(int[][] map, int[] pos, int dir) {
        boolean hasRunOut = false;
        map[pos[0]][pos[1]] = 1;
        int[] next;

        while (!hasRunOut) {
            next = nextStep(pos, dir);

            if (isWithinBounds(map.length, map[0].length, next[0], next[1])) {
                int c = map[next[0]][next[1]];
                if (c < 2) {
                    pos = next;
                    map[pos[0]][pos[1]] = 1;
                    VISITED.add(formatCell(pos, dir));
                } else if (c == 2) {
                    dir++;
                }
            } else {
                hasRunOut = true;
            }

        }
    }

    public static void runPathsPart2(Set<String> resultSet, int[][] map, int[] pos, int dir, Set<String> visited) {
        boolean hasRunOut = false;
        visited.add(formatCell(pos, dir));
        int[] next;

        while (!hasRunOut) {

            next = nextStep(pos, dir);

            if (isWithinBounds(map.length, map[0].length, next[0], next[1])) {
                int c = map[next[0]][next[1]];
                if (c != 2) {
                    pos = next;
                    map[pos[0]][pos[1]] = 1;
                    visited.add(formatCell(pos, dir));

                    if (pos[0] == 2 && pos[1] == 2) {
                        System.out.println();
                    }

                    if (runUntilVisitedOrExit(map, pos, dir, visited)) {
                        int[] block = nextStep(pos, dir);
                        resultSet.add(String.format("%d,%d", block[0], block[1]));
                    }
                } else {
                    dir++;
                }
            } else {
                hasRunOut = true;
            }
        }
    }

    // 6, 4
    public static boolean runUntilVisitedOrExit(int[][] map, int[] pos, int dir, Set<String> visited) {
        int[] block = nextStep(pos, dir);
        if (!isWithinBounds(map.length, map[0].length, block[0], block[1])) return false;
        if (map[block[0]][block[1]] == 2) return false;
        if (map[block[0]][block[1]] == 1) return false;

        int[][] mapCopy = deepCopy(map);
        mapCopy[block[0]][block[1]] = 2;
        Set<String> visitedCopy = new HashSet<>(Set.copyOf(visited));
        boolean hasRunOut = false;
        int[] cur = pos.clone();
        int[] next;

        while (!hasRunOut) {
            next = nextStep(cur, dir);

            if (isWithinBounds(map.length, map[0].length, next[0], next[1])) {
                if (visitedCopy.contains(formatCell(next, dir))) {
                    return true;
                }

                int c = mapCopy[next[0]][next[1]];
                if (c < 2) {
                    cur = next;
                    mapCopy[cur[0]][cur[1]] = 1;
                    visitedCopy.add(formatCell(cur, dir));
                } else if (c == 2) {
                    dir++;
                }
            } else {
                hasRunOut = true;
            }
        }

        return false;
    }

    public static String formatCell(int[] pos, int dir) {
        return String.format("%d,%d,%d", pos[0], pos[1], dir % 4);
    }

    public static int[] nextStep(int[] pos, int dir) {
        int[] next = {pos[0], pos[1]};
        switch (dir % 4) {
            case 0: // UP
                next[0]--;
                break;
            case 1: // RIGHT
                next[1]++;
                break;
            case 2: // DOWN
                next[0]++;
                break;
            case 3: // LEFT
                next[1]--;
                break;
            default:
                System.out.println("DIR ERROR");
                break;
        }
        return next;
    }

    public static boolean isWithinBounds(int ROW_MAX, int COL_MAX, int x, int y) {
        return x >= 0 && x < ROW_MAX && y >= 0 && y < COL_MAX;
    }

    public static void initMap(int[][] map, int[] pos, List<String> list) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                switch (list.get(i).charAt(j)) {
                    case '#':
                        map[i][j] = 2; // blocked
                        break;
                    case '.':
                        map[i][j] = 0; // not visited
                        break;
                    case '^':
                        map[i][j] = 1; // visited
                        pos[0] = i;
                        pos[1] = j;
                        break;
                    default:
                        System.out.println("ERROR");
                        break;

                }
            }
        }
    }

    public static void printMap(int[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                int a = map[i][j];
                if (a == 0) System.out.print(".");
                if (a == 1) System.out.print("1");
                if (a == 2) System.out.print("#");
            }
            System.out.println();
        }
    }

    public static int[][] deepCopy(int[][] arr) {
        int[][] copy = new int[arr.length][arr[0].length];

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                copy[i][j] = arr[i][j];
            }
        }

        return copy;
    }

    public static List<String> readFile() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);
        List<String> map = new ArrayList<>();
        try {
            File file = new File(url.getPath());
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = bf.readLine();

            while (line != null) {
                map.add(line);
                line = bf.readLine();
            }
        } catch (NullPointerException | IOException e) {
            System.exit(1);
        }

        return map;
    }
}
