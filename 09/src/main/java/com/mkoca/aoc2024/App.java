package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class App {
    public static final String FILE_NAME = "input.txt";

    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        int[] disk = createDisk(readFile().get(0));

        compactDisk(disk);
        // printDisk(disk);
        System.out.println("Part 1: " + checkSum(disk));
    }

    public static void part2() {
        int[] disk = createDisk(readFile().get(0));
        Map<Integer, Integer> emptyPages = emptyPages(disk);
        compactDiskByFile(disk, emptyPages);

        // printDisk(disk);
        System.out.println("Part 2: " + checkSum2(disk));
    }

    public static void moveFile(int[] disk, int[] file, int start) {
        int[] tempFile = new int[]{file[0] - file[1] + 1, file[1], file[2]};
        // move file
        for (int i = start; i < start + file[1]; i++) {
            disk[i] = file[2];
        }
        // mark empty old file location
        for (int i = tempFile[0]; i < tempFile[0] + tempFile[1]; i++) {
            disk[i] = -1;
        }
    }

    public static int getEmptyPartition(int left, int len, Map<Integer, Integer> emptyPages) {
        for (var key : emptyPages.keySet()) {
            if (key > left) {
                return -1;
            }

            if (emptyPages.get(key) >= len) {
                return key;
            }
        }

        return -1;
    }

    public static void compactDiskByFile(int[] disk, Map<Integer, Integer> emptyPages) {
        int[] file = {disk.length, 0, -1}; // [start, length, fileId]

        for (int i = disk.length - 1; i >= 0; i--) {
            if (disk[i] != -1) {
                file[0] = i; // start
                file[1] = 0; // length
                file[2] = disk[i]; // file id
                while (i >= 0 && disk[i] == file[2]) {
                    i--;
                    file[1]++;
                }
                i++;

                int key = getEmptyPartition(file[0] - file[1] + 1, file[1], emptyPages);

                if (key != -1) {
                    int emptyBlockSize = emptyPages.get(key);

                    moveFile(disk, file, key);
                    emptyPages.remove(key);

                    if (file[1] < emptyBlockSize) {
                        emptyPages.put(key + file[1], emptyBlockSize - file[1]);
                    }
                }

                file = new int[]{i, 0, -1};
            }
        }
    }

    public static Map<Integer, Integer> emptyPages(int[] disk) {
        Map<Integer, Integer> emptyPageMap = new TreeMap<>();
        int cur = 0;
        for (int i = 0; i < disk.length; i++) {
            cur = i;
            if (disk[i] == -1) {
                while (disk[i] == -1) i++;
                emptyPageMap.put(cur, i - cur);
            }
        }
        return emptyPageMap;
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

    public static long checkSum2(int[] disk) {
        long res = 0;
        for (int i = 0; i < disk.length; i++) {
            if (disk[i] != -1) res += (long) i * disk[i];
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
            if (elem == -1) sb.append('.');
            else sb.append(elem);
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
