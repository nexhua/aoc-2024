package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class App {

    public static final String FILE_NAME = "input.txt";

    public static void main(String[] args) {
        part1();
        part2();
    }


    public static void part1() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);

        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();

        try {
            File file = new File(url.getPath());
            BufferedReader bf = new BufferedReader(new FileReader(file));

            String line = bf.readLine();

            while (line != null) {
                String[] nums = line.split("\\s+");

                left.add(Integer.parseInt(nums[0]));
                right.add(Integer.parseInt(nums[1]));

                line = bf.readLine();
            }

        } catch (NullPointerException | IOException e) {
            System.exit(1);
        }


        left = left.stream().sorted().collect(Collectors.toList());
        right = right.stream().sorted().collect(Collectors.toList());

        long res = 0;
        for (int i = 0; i < left.size(); i++) {
            res += Math.abs(right.get(i) - left.get(i));
        }

        System.out.println("Part 1: " + res);
    }

    public static void part2() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);

        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();

        try {
            File file = new File(url.getPath());
            BufferedReader bf = new BufferedReader(new FileReader(file));

            String line = bf.readLine();

            while (line != null) {
                String[] nums = line.split("\\s+");

                left.add(Integer.parseInt(nums[0]));
                right.add(Integer.parseInt(nums[1]));

                line = bf.readLine();
            }

        } catch (NullPointerException | IOException e) {
            System.exit(1);
        }


        HashMap<Integer, Integer> rightCounter = new HashMap<Integer, Integer>();

        for (int num : right) {
            Integer cnt = rightCounter.get(num);

            if (cnt != null) {
                rightCounter.put(num, ++cnt);
            } else {
                rightCounter.put(num, 1);
            }
        }

        long similarityScore = 0;

        for (var num : left) {
            Integer count = rightCounter.get(num);

            if (count != null) {
                similarityScore += num * count;
            }
        }

        System.out.println("Part 2: " + similarityScore);
    }
}
