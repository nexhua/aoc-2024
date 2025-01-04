package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class App {
    public static final String FILE_NAME = "input.txt";
    public static final int[][] DIRECTIONS = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // UP, RIGHT, DOWN, LEFT

    public static void main(String[] args) {
        part1();
        // part2();
    }

    public static void part1() {
        Cell start = new Cell(0, 0);
        Cell end = new Cell(0, 0);
        char[][] map = createMap(readFile(), start, end);
        
        List<Cell> path = dijkstra(map, start, end);
        // printMap(map, start, end);
        // printPath(map, path, start, end);
        System.out.println("Part 1: " + path.getLast().cost);
    }

    public static void part2() {

    }

    public static List<Cell> dijkstra(char[][] map, Cell from, Cell end) {
        Cell[][] cells = new Cell[map.length][map[0].length];
        Queue<Cell> queue = new ArrayDeque<>();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                Cell c = new Cell(i, j);
                if (map[i][j] == '.') {
                    queue.add(c);
                    cells[i][j] = c;
                }
            }
        }

        cells[from.x][from.y].cost = 0L;
        cells[from.x][from.y].withDir = 1;

        while (!queue.isEmpty()) {
            Cell cur = minDist(cells);

            queue.remove(cur);
            cur.visited = true;

            for (var neigh : getNeighbours(map, cur)) {
                Cell neighbour = cells[neigh[0]][neigh[1]];
                // if (neighbour.visited) continue;

                long cost = getCost(cur.withDir, neigh[2]); // cost from cur to neighbour

                if (cur.cost + cost < neighbour.cost) {
                    neighbour.cost = cur.cost + cost;
                    neighbour.from = cur;
                    neighbour.withDir = neigh[2];
                }
            }
        }

        return buildPath(cells[end.x][end.y]);
    }

    public static List<Cell> buildPath(Cell end) {
        Cell cur = end;
        List<Cell> path = new ArrayList<>();

        while (cur != null) {
            path.add(cur);
            cur = cur.from;
        }

        return path.reversed();
    }

    public static long getCost(int currentDir, int neighbourDir) {
        if (Math.abs(currentDir - neighbourDir) == 3) return 1001L;
        return Math.min(Math.abs(currentDir - neighbourDir), Math.abs(neighbourDir - currentDir)) * 1000L + 1L;
    }

    public static Cell minDist(Cell[][] cells) {
        long min = Long.MAX_VALUE;
        Cell found = null;

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                Cell cur = cells[i][j];

                if (cur != null && cur.isNotVisited() && cur.cost < min) {
                    found = cur;
                    min = found.cost;
                }
            }
        }

        if (found == null) {
            throw new IllegalStateException("Could not find a min value node");
        }

        return found;
    }

    // returns [x, y, dir] of all valid neighbours
    public static List<int[]> getNeighbours(char[][] map, Cell c) {
        List<int[]> neighbours = new ArrayList<>();

        for (int i = 0; i < DIRECTIONS.length; i++) {
            int[] dir = DIRECTIONS[i];
            int x = c.x + dir[0];
            int y = c.y + dir[1];

            if (withinBounds(map, x, y) && map[x][y] == '.') neighbours.add(new int[]{x, y, i});
        }

        return neighbours;
    }

    public static boolean withinBounds(char[][] map, int x, int y) {
        return (x >= 0 && x < map.length) && (y >= 0 && y < map[0].length);
    }

    public static void printPath(char[][] map, List<Cell> path, Cell start, Cell end) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (i == start.x && j == start.y) {
                    System.out.print('S');
                    continue;
                }

                if (i == end.x && j == end.y) {
                    System.out.print('E');
                    continue;
                }

                int finalI = i;
                int finalJ = j;
                Optional<Cell> cellInPath = path.stream().filter(c -> c.x == finalI && c.y == finalJ).findFirst();

                if (cellInPath.isPresent()) {
                    Cell c = cellInPath.get();
                    if (c.cost != Long.MAX_VALUE) {
                        if (c.withDir == 0) {
                            System.out.print("^");
                        } else if (c.withDir == 1) {
                            System.out.print(">");

                        } else if (c.withDir == 2) {
                            System.out.print("v");

                        } else if (c.withDir == 3) {
                            System.out.print("<");

                        } else {
                            System.out.print("ERROR");
                        }
                    }
                } else {
                    System.out.print(map[i][j]);
                }
            }
            System.out.println();
        }
    }

    public static void printMap(char[][] map, Cell start, Cell end) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (i == start.x && j == start.y) {
                    System.out.print('S');
                    continue;
                }

                if (i == end.x && j == end.y) {
                    System.out.print('E');
                    continue;
                }
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    public static char[][] createMap(List<String> input, Cell start, Cell end) {
        char[][] map = new char[input.size()][input.getFirst().length()];

        for (int i = 0; i < map.length; i++) {
            String row = input.get(i);
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = row.charAt(j);

                if (map[i][j] == 'S') {
                    start.x = i;
                    start.y = j;
                    map[i][j] = '.';
                }

                if (map[i][j] == 'E') {
                    end.x = i;
                    end.y = j;
                    map[i][j] = '.';
                }
            }
        }

        return map;
    }

    public static List<String> readFile() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);
        List<String> list = new ArrayList<>();
        try {
            assert url != null;
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