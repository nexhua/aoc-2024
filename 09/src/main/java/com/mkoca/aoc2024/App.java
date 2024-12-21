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
        int[] disk = createDisk(readFile().get(0));

        compactDisk(disk);
        // printDisk(disk);
        System.out.println("Part 1: " + checkSum(disk));
    }

    public static long checkSum(int[] disk) {
        long res = 0;
        for (int i = 0; i < disk.length; i++) {
            if (disk[i] == -1) {
                break;
            }
            res += (long) i * disk[i];
        }

        return res;
    }

    public static void compactDisk(int[] disk) {
        int empty = nextEmpty(disk, -1);
        int right = nextBlock(disk, disk.length);

        while (empty < right) {
            disk[empty] = disk[right];
            disk[right] = -1;

            empty = nextEmpty(disk, -1);
            right = nextBlock(disk, disk.length);
        }
    }

    public static int nextEmpty(int[] disk, int after) {
        for (int i = after + 1; i < disk.length; i++) {
            if (disk[i] == -1) return i;
        }
        return Integer.MAX_VALUE;
    }

    public static int nextBlock(int[] disk, int before) {
        for (int i = before - 1; i >= 0; i--) {
            if (disk[i] != -1) return i;
        }

        return Integer.MIN_VALUE;
    }

    public static void printDisk(int[] disk) {
        StringBuilder sb = new StringBuilder(disk.length);

        for (var elem : disk) {
            if (elem == -1)
                sb.append('.');
            else
                sb.append(elem);
        }

        System.out.println(sb);
    }

    public static int[] createDisk(String input) {
        boolean isEmpty = false;
        int len = 0;
        for (int i = 0; i < input.length(); i++) {
            len += Character.digit(input.charAt(i), 10);
            isEmpty = !isEmpty;
        }

        int[] disk = new int[len];
        isEmpty = false;
        int fileId = 0;
        int diskPos = 0;
        for (int i = 0; i < input.length(); i++) {
            int posLen = Character.digit(input.charAt(i), 10);
            if (!isEmpty) {
                for (int j = 0; j < posLen; j++) {
                    disk[diskPos] = fileId;
                    diskPos++;
                }
                fileId++;
            } else {
                for (int j = 0; j < posLen; j++) {
                    disk[diskPos] = -1;
                    diskPos++;
                }
            }

            isEmpty = !isEmpty;
        }

        return disk;
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
