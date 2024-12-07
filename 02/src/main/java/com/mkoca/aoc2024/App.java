package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {
    public static final String FILE_NAME = "input.txt";


    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        ArrayList<List<Integer>> reports = readFile();
        int safeReports = 0;

        int i = 0;
        for (; i < reports.size(); i++) {
            if (isReportSafe(reports.get(i))) safeReports++;

        }

        System.out.println("Part 1: " + safeReports);
    }

    public static void part2() {
        ArrayList<List<Integer>> reports = readFile();
        int safeReports = 0;

        int i = 0;
        for (; i < reports.size(); i++) {
            if (isReportSafe(reports.get(i))) safeReports++;
            else {
                if (isPermutationReportSafe(reports.get(i))) safeReports++;
            }
        }

        System.out.println("Part 2: " + safeReports);
    }

    public static boolean isReportSafe(List<Integer> report) {
        int j = 1, dir = 0, diff = 0;
        int diffPassCnt = 0;

        for (; j < report.size(); j++) {
            diff = report.get(j - 1) - report.get(j);
            if (diff > 0) {
                dir += 1;
            } else if (diff < 0) {
                dir -= 1;
            }

            diff = Math.abs(diff);

            if (diff >= 1 && diff < 4) {
                diffPassCnt++;
            }
        }

        dir = Math.abs(dir);

        // System.out.printf("%s - dir: %d, diffCnt: %d%n", report, dir, diffPassCnt);
        return Math.abs(dir) == report.size() - 1 && diffPassCnt == report.size() - 1;
    }

    public static boolean isPermutationReportSafe(List<Integer> report) {
        for (int i = 0; i < report.size(); i++) {
            List<Integer> perm = new ArrayList<>(report);
            perm.remove(i);

            boolean isPermutationSafe = isReportSafe(perm);

            if (isPermutationSafe) {
                return true;
            }
        }

        return false;
    }

    public static ArrayList<List<Integer>> readFile() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);
        ArrayList<List<Integer>> list = new ArrayList<>();

        try {
            File file = new File(url.getPath());
            BufferedReader bf = new BufferedReader(new FileReader(file));

            String line = bf.readLine();

            while (line != null) {
                String[] nums = line.split("\\s+");
                list.add(Arrays.stream(nums).map(Integer::parseInt).collect(Collectors.toList()));
                line = bf.readLine();
            }

        } catch (NullPointerException | IOException e) {
            System.exit(1);
        }

        return list;
    }
}
