package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class App {
    public static final String FILE_NAME = "sampleTree.txt";

    static int ROW_SIZE;
    static int COL_SIZE;
    static int VERTICAL_MIDDLE;


    static {
        if (FILE_NAME.startsWith("sample")) {
            COL_SIZE = 11;
            ROW_SIZE = 7;
            VERTICAL_MIDDLE = COL_SIZE / 2;
        } else {
            COL_SIZE = 101;
            ROW_SIZE = 103;
            VERTICAL_MIDDLE = COL_SIZE / 2;
        }
    }

    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        List<Robot> robots = getRobots(readFile());
        for (var r : robots) {
            // X is for column, Y is for row
            for (int i = 0; i < 100; i++) step(r);
        }

        // printMap(createMap(robots));
        long res = getResults(robots);
        System.out.println("Part 1: " + res);
    }


    public static void part2() {
        List<Robot> robots = getRobots(readFile());
        boolean treeFound = false;

        long sec = 0;
        while (!treeFound) {
            for (var r : robots) step(r);

            // check
            // to detect christmas tree, split in the middle vertically and check if EVERY point (that is not on the vertical line) is equidistant to the vertical middle line
            treeFound = areAllEquidistant(robots);
            sec++;

            if(sec % 1_000_000L == 0) System.out.println(sec);
        }

        // printMap(createMap(robots));
        System.out.println("Part 2: " + sec);
    }

    static boolean areAllEquidistant(List<Robot> robots) {
        int[][] map = createMap(robots);

        int highestRobotRow = IntStream.range(0, ROW_SIZE).filter(i -> map[i][VERTICAL_MIDDLE] != 0).findFirst().orElse(-1);

        if(highestRobotRow == -1) {
            return false;
        }

        // if highest robot exits, check if every other robot is below it
        int maxRow = robots.stream().filter(r -> !(r.c.x == VERTICAL_MIDDLE && r.c.y == highestRobotRow)).map(r -> r.c.y).min(Integer::compare).orElseThrow();

        if (maxRow <= highestRobotRow) {
            return false;
        }

        for (int i = highestRobotRow; i < ROW_SIZE; i++) {
            boolean rowEq = isRowEquidistant(map[i]);

            if (!rowEq) {
                return false;
            }
        }

        return true;
    }

    static boolean isRowEquidistant(int[] row) {

        for (int i = 0; i < COL_SIZE; i++) {
            if (i != VERTICAL_MIDDLE && row[i] != 0) {
                int diff = VERTICAL_MIDDLE - i;

                if (row[VERTICAL_MIDDLE + diff] == 0) {
                    return false;
                }
            }
        }

        return true;
    }


    static long getResults(List<Robot> robots) {
        long res = 1;

        int middleRow = ROW_SIZE / 2;
        int middleCol = COL_SIZE / 2;

        //  0  | 1
        // ----|----
        //  2  | 3
        // COL LEFT = 0 point, COL RIGHT = 1 point
        // ROW ABOVE = 0 point, ROW BELOW = 2 point
        long[] quadrants = new long[4];

        for (var r : robots) {
            if (r.c.x == middleCol || r.c.y == middleRow) continue;

            int colPoint = r.c.x < middleCol ? 0 : 1;
            int rowPoint = r.c.y < middleRow ? 0 : 2;

            quadrants[colPoint + rowPoint]++;
        }


        for (var q : quadrants) {
            res *= q;
        }
        return res;
    }

    static void step(Robot r) {
        r.c.x += r.v.x;
        r.c.y += r.v.y;

        if (r.c.x < 0) {
            r.c.x = COL_SIZE + r.c.x;
        } else {
            r.c.x %= COL_SIZE;
        }

        if (r.c.y < 0) {
            r.c.y = ROW_SIZE + r.c.y;
        } else {
            r.c.y %= ROW_SIZE;
        }
    }

    static int[][] createMap(List<Robot> robots) {
        int[][] map = new int[ROW_SIZE][COL_SIZE];
        for (var r : robots) {
            map[r.c.y][r.c.x]++;
        }

        return map;
    }

    static void printMap(int[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 0) System.out.print(".");
                else System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    static List<Robot> getRobots(List<String> input) {
        List<Robot> robots = new ArrayList<>();

        for (var r : input) {
            String[] pos = r.split(" ");
            int sep = pos[0].indexOf(',');

            int x = Integer.parseInt(pos[0].substring(2, sep));
            int y = Integer.parseInt(pos[0].substring(sep + 1));

            Cell start = new Cell(x, y);

            sep = pos[1].indexOf(',');
            x = Integer.parseInt(pos[1].substring(2, sep));
            y = Integer.parseInt(pos[1].substring(sep + 1));

            Cell velocity = new Cell(x, y);

            robots.add(new Robot(start, velocity));
        }

        return robots;
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

class Robot {
    Cell s; // starting position
    Cell v; // velocity of robot
    Cell c; // current position

    public Robot(Cell s, Cell v) {
        this.s = s;
        this.v = v;
        this.c = new Cell(s.x, s.y);
    }

    @Override
    public String toString() {
        return String.format("p=%s v=%s c=%s", s, v, c);
    }
}

class Cell {
    int x; // x is col
    int y; // y is row

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("%d,%d", x, y);
    }
}
