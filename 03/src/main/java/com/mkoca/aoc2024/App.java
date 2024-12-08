package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static final String FILE_NAME = "input.txt";

    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        Pattern pattern = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)", Pattern.MULTILINE);

        ArrayList<String> lines = readFile();

        long result = 0;
        for (var s : lines) {
            Matcher matcher = pattern.matcher(s);

            while (matcher.find()) {
                String op = matcher.group();
                Long[] nums = Arrays.stream(op.substring(4, op.length() - 1).split(",")).map(Long::parseLong).toArray(Long[]::new);
                result += nums[0] * nums[1];
            }
        }

        System.out.println("Part 1: " + result);

    }

    public static void part2() {
        Pattern pattern = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)|do\\(\\)|don't\\(\\)", Pattern.MULTILINE);

        ArrayList<String> lines = readFile();

        long result = 0;
        boolean active = true;
        for (var s : lines) {
            Matcher matcher = pattern.matcher(s);

            while (matcher.find()) {
                String op = matcher.group();
                if (op.startsWith("mul")) {
                    if (!active) continue;
                    Long[] nums = Arrays.stream(op.substring(4, op.length() - 1).split(",")).map(Long::parseLong).toArray(Long[]::new);
                    result += nums[0] * nums[1];
                } else if (op.startsWith("don't")) {
                    active = false;
                } else if (op.startsWith("do")) {
                    active = true;
                } else {
                    System.out.println("Unreachable");
                }
            }
        }

        System.out.println("Part 2: " + result);

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
