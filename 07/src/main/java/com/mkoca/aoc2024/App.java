package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class App {
    public static final String FILE_NAME = "input.txt";

    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        List<String> lines = readFile();
        Map<Integer, List<String>> operationsListMap = new HashMap<>();
        final List<Character> operations = List.of('+', '*');

        Long res = 0L;
        for (var line : lines) {
            String[] nums = line.split(":");
            Long target = Long.parseLong(nums[0]);
            List<Integer> operands = Arrays.stream(nums[1].trim().split(" ")).map(Integer::parseInt).toList();

            if (isTargetReachable(operationsListMap, target, operands, operations)) {
                res += target;
            }

            // System.out.printf("%d %s\n", target, operands);
        }

        System.out.printf("Part 1: %d\n", res);
    }

    public static void part2() {
        List<String> lines = readFile();
        Map<Integer, List<String>> operationsListMap = new HashMap<>();
        final List<Character> operations = List.of('+', '*', '|');

        Long res = 0L;
        for (var line : lines) {
            String[] nums = line.split(":");
            Long target = Long.parseLong(nums[0]);
            List<Integer> operands = Arrays.stream(nums[1].trim().split(" ")).map(Integer::parseInt).toList();

            if (isTargetReachable(operationsListMap, target, operands, operations)) {
                res += target;
            }

            // System.out.printf("%d %s\n", target, operands);
        }

        System.out.printf("Part 2: %d\n", res);
    }


    public static boolean isTargetReachable(Map<Integer, List<String>> operationsListMap, Long target, List<Integer> operands, List<Character> operations) {
        List<String> operationsList = getOperationList(operationsListMap, operations, operands.size() - 1);

        for (var ops : operationsList) {
            Stack<Long> acc = new Stack<>();

            for (int i = operands.size() - 1; i >= 0; i--) {
                acc.push((long) operands.get(i));
            }


            for (int i = 0; i < ops.length(); i++) {
                switch (ops.charAt(i)) {
                    case '+':
                        acc.push(acc.pop() + acc.pop());
                        break;
                    case '*':
                        acc.push(acc.pop() * acc.pop());
                        break;
                    case '|':
                        acc.push(Long.parseLong(String.valueOf(acc.pop()).concat(String.valueOf(acc.pop()))));
                        break;
                    default:
                        System.out.println("ERROR");
                        break;
                }
            }

            Long opRes = acc.pop();
            if (Objects.equals(opRes, target)) {
                return true;
            }
        }

        return false;
    }

    public static List<String> getOperationList(Map<Integer, List<String>> operationsListMap, List<Character> operations, int operandCount) {
        List<String> found = operationsListMap.get(operandCount);
        if (found != null) {
            return found;
        }

        found = createOperationList(operations, operandCount);
        operationsListMap.put(operandCount, found);
        return found;
    }

    public static List<String> createOperationList(List<Character> operations, int operandCount) {
        List<List<Character>> lists = new ArrayList<>();
        for (int i = 0; i < operandCount; i++) {
            lists.add(operations);
        }

        List<String> permRes = new ArrayList<>();
        generatePermutations(lists, permRes, 0, "");

        return permRes;
    }

    // ref https://stackoverflow.com/questions/17192796/generate-all-combinations-from-multiple-lists
    public static void generatePermutations(List<List<Character>> lists, List<String> result, int depth, String current) {
        if (depth == lists.size()) {
            result.add(current);
            return;
        }

        for (int i = 0; i < lists.get(depth).size(); i++) {
            generatePermutations(lists, result, depth + 1, current + lists.get(depth).get(i));
        }
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
