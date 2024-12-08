package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    public static final String FILE_NAME = "input.txt";

    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        Map<String, Node> nodes = new HashMap<>();
        List<ArrayList<String>> reports = new ArrayList<>();
        readFile(nodes, reports);

        long res = 0;
        for (var report : reports) {
            boolean passed = checkReport(nodes, report);
            if (passed) {
                res += Long.parseLong(report.get(report.size() / 2));
            }
        }

        System.out.println("Part 1: " + res);
    }

    public static void part2() {
        Map<String, Node> nodes = new HashMap<>();
        List<ArrayList<String>> reports = new ArrayList<>();
        List<ArrayList<String>> badReports = new ArrayList<>();
        readFile(nodes, reports);

        // printDependencyList(nodes);

        for (var report : reports) {
            // accumulate part 1 failures
            boolean passed = checkReport(nodes, report);
            if (!passed) {
                badReports.add(report);
            }
        }

        // actual part 2 logic
        long res = 0;
        for (var badReport : badReports) {
            List<String> reordered = reorder(nodes, badReport);
            if (checkReport(nodes, reordered)) {
                res += Long.parseLong(reordered.get(reordered.size() / 2));
            } else {
                System.out.println("Reordered actually not fixed! " + reordered);
                break;
            }

        }

        System.out.println("Part 2: " + res);
    }

    public static List<String> reorder(Map<String, Node> nodes, List<String> report) {
        resetNodes(nodes);

        List<String> reordered = new ArrayList<>();

        //  mark current report nodes
        for (var val : report) {
            nodes.get(val).setPartOfReport(true);
        }

        int len = report.size();
        while (len > 0) {
            Optional<Node> candidate = report.stream().map(nodes::get).filter(n -> !n.isVisited()).filter(Node::isEligible).findFirst();

            if (candidate.isPresent()) {
                Node cur = candidate.get();
                cur.setVisited(true);
                reordered.add(cur.getValue());
            } else {
                System.out.println("ERROR - No candidate left");
                break;
            }
            len--;
        }

        assert reordered.size() == report.size();

        return reordered;
    }

    public static boolean checkReport(Map<String, Node> nodes, List<String> report) {
        resetNodes(nodes);

        for (var val : report) {
            nodes.get(val).setPartOfReport(true);
        }

        boolean passed = true;

        for (var val : report) {
            Node cur = nodes.get(val);
            cur.setVisited(true);

            if (!cur.isEligible()) {
                passed = false;
                break;
            }
        }

        return passed;
    }

    public static void resetNodes(Map<String, Node> nodes) {
        for (var node : nodes.values()) {
            node.setVisited(false);
            node.setPartOfReport(false);
        }
    }

    public static void printDependencyList(Map<String, Node> nodes) {
        for (var node : nodes.values()) {
            System.out.printf("%s -> (%s)\n", node.getValue(), node.getDependencies().stream().map(Object::toString).collect(Collectors.joining(", ")));
        }
    }

    public static void readFile(Map<String, Node> nodes, List<ArrayList<String>> reports) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);
        try {
            File file = new File(url.getPath());
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = bf.readLine();
            boolean emptyEncounter = false;
            while (line != null) {
                if (line.isEmpty()) {
                    emptyEncounter = true;
                    line = bf.readLine();
                }

                // read dependency list -> 47|53 means that page number 47 must be printed at some point before page number 53
                if (!emptyEncounter) {
                    // System.out.println("-> " + line);
                    String[] newNodes = Arrays.stream(line.split("\\|")).toArray(String[]::new);

                    Node left = nodes.get(newNodes[0]);
                    Node right = nodes.get(newNodes[1]);

                    if (right != null) {
                        // right node exists
                        if (left != null) {
                            // if left also exists, add left to rights dependency list
                            right.getDependencies().add(left);
                        } else {
                            // left does not exist, create and add to rights dependency list
                            left = new Node(newNodes[0]);
                            nodes.put(left.getValue(), left);
                            right.getDependencies().add(new Node(newNodes[0]));
                        }
                    } else {
                        // right node does not exist
                        if (left == null) {
                            // if left node also does not exist, create both and add left to rights dependency list
                            left = new Node(newNodes[0]);
                            right = new Node(newNodes[1]);
                            nodes.put(left.getValue(), left);
                            nodes.put(right.getValue(), right);

                            right.getDependencies().add(left);
                        } else {
                            // if left node exists, create right and add left to rights dependency list
                            right = new Node(newNodes[1]);
                            nodes.put(right.getValue(), right);

                            right.getDependencies().add(left);
                        }
                    }

                } else {
                    // read reports
                    reports.add(Arrays.stream(line.split(",")).collect(Collectors.toCollection(ArrayList::new)));
                }

                line = bf.readLine();
            }
        } catch (NullPointerException | IOException e) {
            System.exit(1);
        }
    }
}
