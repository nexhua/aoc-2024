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
    public static final String FILE_NAME = "sample.txt";

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
        // printMap(map);

        System.out.println("Part 1: " + res);
    }

    public static void part2() {
        List<String> list = readFile();
        int[][] map = new int[list.size()][list.get(0).length()];
        int[] pos = {0, 0};
        int dir = 0;
        Set<String> resultSet = new HashSet<>();

        initMap(map, pos, list);
        runPathsPart2(resultSet, map, pos, dir);

        System.out.println("Results");
        for (var resultPos : resultSet) {
            System.out.println(resultPos);
        }

        // printMap(map);
        System.out.println("Part 2: " + resultSet.size());
    }

    public static boolean linearCheckObstacle(int[][] map, int[] begin, int dir) {
        boolean withinBounds = true;
        int[] next = begin;

        System.out.printf("Starting linear check at: %d,%d\n", nextStep(next, dir)[0], nextStep(next, dir)[1]);

        while (withinBounds) {
            next = nextStep(next, dir);

            if (isWithinBounds(map.length, map[0].length, next[0], next[1])) {
                if (map[next[0]][next[1]] == 2) {
                    System.out.printf("Linear match at : %d,%d\n", next[0], next[1]);
                    return true;
                }
            } else {
                withinBounds = false;
            }
        }

        return false;
    }


    public static boolean linearCheckConnectionToVisited(int[][] map, int[] begin, int dir) {
        boolean withinBounds = true;
        int[] next = begin;
        int[] prev = null;

        while (withinBounds) {
            next = nextStep(next, dir);

            if (isWithinBounds(map.length, map[0].length, next[0], next[1])) {
                if (prev != null && map[next[0]][next[1]] == 2) {
                    System.out.printf("Obstacle connected : %d,%d\n", prev[0], prev[1]);
                    return map[prev[0]][prev[1]] == 1;
                }
            } else {
                withinBounds = false;
            }

            prev = next;
        }

        return false;
    }

    public static void runPathsPart2(Set<String> resultSet, int[][] map, int[] pos, int dir) {
        boolean hasRunOut = false;
        map[pos[0]][pos[1]] = 1;
        int[] next;

        while (!hasRunOut) {
            next = nextStep(pos, dir);

            if (isWithinBounds(map.length, map[0].length, next[0], next[1])) {
                int c = map[next[0]][next[1]];
                if (c == 0) {
                    pos = next;
                    map[pos[0]][pos[1]] = 1;

                    if (linearCheckConnectionToVisited(map, pos, dir + 1)) {
                        int[] blockPos = nextStep(pos, dir);
                        resultSet.add(String.format("%d,%d", blockPos[0], blockPos[1]));
                    }
                } else if (c == 1) {
                    pos = next;

                    System.out.printf("Previous visited node at: %d,%d\n", pos[0], pos[1]);
                    // update dir and check
                    if (linearCheckObstacle(map, pos, dir + 1)) {
                        int[] blockPos = nextStep(pos, dir);
                        resultSet.add(String.format("%d,%d", blockPos[0], blockPos[1]));
                    }
                } else if (c == 2) {
                    dir++;
                }
            } else {
                hasRunOut = true;
            }

        }
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
                } else if (c == 2) {
                    dir++;
                }
            } else {
                hasRunOut = true;
            }

        }
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
