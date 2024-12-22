package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class App {
    public static final String FILE_NAME = "sample.txt";

    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        List<String> input = readFile();
        List<String> stones = new ArrayList<>(List.of(input.getFirst().split(" ")));

        for (int i = 0; i < 25; i++) {
            stones = step(stones);
        }

        System.out.println("Part 1: " + stones.size());
    }

    public static void part2() {
        List<String> input = readFile();
        List<Integer> stones = Arrays.stream(input.getFirst().split(" ")).map(Integer::parseInt).toList();
        Map<Integer, Integer> counter = new HashMap<>();
        Map<Integer, int[]> stepCache = new HashMap<>();

        for (var stone : stones) {
            counter.put(stone, counter.getOrDefault(stone, 0) + 1);
        }

        for (int i = 0; i < 75; i++) {
            counter = step(counter, stepCache);
        }

        long res = 0;
        for (var cnt : counter.values()) {
            if (cnt > 0) res += cnt;
        }

        System.out.println("Part 2: " + res);
    }


    public static int[] getStep(int n, Map<Integer, int[]> c) {
        int[] res = c.get(n);
        if (res != null) {
            return res;
        }

        if (n == 0) {
            res = new int[]{1};
            c.put(n, res);
            return res;
        }

        String s = String.valueOf(n);
        if (s.length() % 2 == 0) {
            String leftS = s.substring(0, s.length() / 2);
            String rightS = String.valueOf(Integer.parseInt(s.substring(s.length() / 2)));

            res = new int[]{Integer.parseInt(leftS), Integer.parseInt(rightS)};
            c.put(n, res);
            return res;
        }

        res = new int[]{2024 * n};
        c.put(n, res);
        return res;
    }

    public static Map<Integer, Integer> step(Map<Integer, Integer> counter, Map<Integer, int[]> stepCache) {
        Map<Integer, Integer> newCounter = new HashMap<>();
        for (var key : counter.keySet()) {
            int cnt = counter.get(key);

            int[] res = getStep(key, stepCache);
            if (res.length == 2) {
                newCounter.put(res[1], newCounter.getOrDefault(res[1], 0) + cnt);
            }
            newCounter.put(res[0], newCounter.getOrDefault(res[0], 0) + cnt);
        }

        return newCounter;
    }

    public static List<String> step(List<String> stones) {
        List<String> after = new ArrayList<>();

        for (var stone : stones) {
            if ("0".equals(stone)) {
                after.add("1");
            } else if (stone.length() % 2 == 0) {
                after.add(stone.substring(0, stone.length() / 2));
                after.add(String.valueOf(Integer.parseInt(stone.substring(stone.length() / 2))));
            } else {
                long val = 2024L * Long.parseUnsignedLong(stone);
                after.add(Long.toUnsignedString(val));
            }
        }
        return after;
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
