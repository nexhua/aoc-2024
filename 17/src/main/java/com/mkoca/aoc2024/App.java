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
        // part2();
    }

    public static void part1() {
        CPU cpu = createCPU(readFile());

        while (!cpu.HALTED) {
            cpu.operate();
        }

        System.out.println("Part 1: " + cpu.getOut());
    }

    public static void part2() {

    }

    public static CPU createCPU(List<String> input) {
        assert input.size() == 5;
        long a = Long.parseLong(input.get(0).substring(input.get(0).indexOf(':') + 2));
        long b = Long.parseLong(input.get(1).substring(input.get(1).indexOf(':') + 2));
        long c = Long.parseLong(input.get(2).substring(input.get(2).indexOf(':') + 2));
        List<Pair<Long>> instructions = new ArrayList<>();
        String[] ins = input.get(4).substring(input.get(4).indexOf(':') + 2).split(",");

        int i = 0;
        while (i < ins.length) {
            long first = Long.parseLong(ins[i]);
            i++;
            long second = Long.parseLong(ins[i]);
            i++;
            instructions.add(new Pair<>(first, second));
        }

        return new CPU(a, b, c, instructions);
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
