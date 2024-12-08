package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static final String FILE_NAME = "input.txt";

    public static void main(String[] args) {
        part1();
    }

    public static void part1() {
        List<String> list = new ArrayList<>();
        readFile(list);
        int[][] map = new int[list.size()][list.get(0).length()];
        int[] pos = {0, 0};
        int dir = 0;
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

    public static void readFile(List<String> map) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);
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
    }
}
