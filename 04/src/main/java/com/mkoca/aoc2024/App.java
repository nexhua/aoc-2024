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

    public static final String XMAS = "XMAS";
    public static final String SAMX = "SAMX";
    public static final String SAM = "SAM";
    public static final String MAS = "MAS";

    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        Set<String> matches = new HashSet<>();
        List<String> lines = readFile();

        // horizontal
        for (int i = 0; i < lines.size(); i++) {
            String s = lines.get(i);
            for (int j = 0; j <= s.length() - 4; j++) {
                String sub = s.substring(j, j + 4);

                if (sub.equals(XMAS) || sub.equals(SAMX)) {
                    matches.add(format(i, j, i, j + 4));
                    // System.out.printf("%s - %s\n", format(i, j, i, j + 4), sub);
                }
            }
        }

        // vertical
        for (int i = 0; i <= lines.size() - 4; i++) {
            for (int j = 0; j < lines.get(i).length(); j++) {
                String slice = verticalSlice(lines, i, j, 4);

                if (slice.equals(XMAS) || slice.equals(SAMX)) {
                    matches.add(format(i, j, i + 4, j));
                    // System.out.printf("%s - %s\n", format(i, j, i + 4, j), slice);
                }
            }
        }

        // diagonal
        for (int i = 0; i <= lines.size() - 4; i++) {
            for (int j = 0; j <= lines.get(i).length() - 4; j++) {
                checkDiagonal(lines, i, j, matches);
            }
        }

        System.out.println("Part 1: " + matches.size());
    }

    private static String verticalSlice(List<String> list, int beginRow, int col, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(list.get(beginRow).charAt(col));
            beginRow++;
        }
        return sb.toString();
    }

    private static String flatten(List<String> list, int x1, int y1) {
        StringBuilder sb = new StringBuilder();
        for (int i = x1; i < x1 + 4; i++) {
            sb.append(list.get(i), y1, y1 + 4);
        }

        return sb.toString();
    }

    private static void checkDiagonal(List<String> list, int x1, int y1, Set<String> matches) {
        StringBuilder crossRight = new StringBuilder();
        StringBuilder crossLeft = new StringBuilder();

        String linearBox = flatten(list, x1, y1);

        int cnt = XMAS.length();

        int i = 0;
        for (; i < cnt; i++) {
            crossLeft.append(linearBox.charAt(i * cnt + i));
        }

        if (XMAS.contentEquals(crossLeft) || SAMX.contentEquals(crossLeft)) {
            // System.out.printf("%s - %s\n", format(x1, y1, x1 + 4, y1 + 4), crossLeft);
            matches.add(format(x1, y1, x1 + 4, y1 + 4));
        }

        i = 0;
        int y = cnt - 1;
        for (; i < cnt; i++) {
            crossRight.append(linearBox.charAt(i * cnt + y));
            y--;
        }

        if (XMAS.contentEquals(crossRight) || SAMX.contentEquals(crossRight)) {
            // System.out.printf("%s - %s\n", format(x1, y1 + 4, x1 + 4, y1), crossRight);
            matches.add(format(x1, y1 + 4, x1 + 4, y1));
        }
    }

    private static String format(int x1, int y1, int x2, int y2) {
        return String.format("(%d,%d)->(%d,%d)", x1, y1, x2, y2);
    }

    private static void part2() {
        Set<String> matches = new HashSet<>();
        List<String> lines = readFile();

        int i, j;

        for (i = 0; i <= lines.size() - SAM.length(); i++) {
            for (j = 0; j <= lines.get(i).length() - SAM.length(); j++) {
                StringBuilder crossLeft = new StringBuilder();
                StringBuilder crossRight = new StringBuilder();

                crossLeft.append(lines.get(i).charAt(j));
                crossLeft.append(lines.get(i + 1).charAt(j + 1));
                crossLeft.append(lines.get(i + 2).charAt(j + 2));

                crossRight.append(lines.get(i).charAt(j + 2));
                crossRight.append(lines.get(i + 1).charAt(j + 1));
                crossRight.append(lines.get(i + 2).charAt(j));

                if ((crossLeft.toString().contentEquals(SAM) || crossLeft.toString().contentEquals(MAS)) && (crossRight.toString().contentEquals(SAM) || crossRight.toString().contentEquals(MAS))) {
                    matches.add(format(i, j, i + 2, j + 2));
                }
            }
        }

        System.out.println("Part 2: " + matches.size());
    }

    public static ArrayList<String> readFile() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);
        ArrayList<String> lines = new ArrayList<>();
        try {
            File file = new File(url.getPath());
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = bf.readLine();
            while (line != null) {
                lines.add(line);
                line = bf.readLine();
            }
        } catch (NullPointerException | IOException e) {
            System.exit(1);
        }
        return lines;
    }
}
