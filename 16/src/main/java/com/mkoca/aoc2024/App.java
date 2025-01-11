package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class App {
    public static final String FILE_NAME = "input.txt";
    public static final int[][] DIRECTIONS = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // UP, RIGHT, DOWN, LEFT

    public static void main(String[] args) {
        Cell[][] distances = part1();
        part2(distances);
    }

    public static Cell[][] part1() {
        Cell start = new Cell(0, 0);
        Cell end = new Cell(0, 0);
        char[][] map = createMap(readFile(), start, end);

        Cell[][] cells = dijkstra(map, start);
        List<Cell> path = buildPath(cells[end.x][end.y]);
        // printMap(map, start, end);
        // printPath(map, path, start, end);
        System.out.println("Part 1: " + path.getLast().cost);
        return cells;
    }

    public static void part2(Cell[][] cells) {
        Cell start = new Cell(0, 0);
        Cell end = new Cell(0, 0);
        char[][] map = createMap(readFile(), start, end);

        List<Cell> originalPath = buildPath(cells[end.x][end.y]);
        Set<Cell> originalPathSet = new HashSet<>(originalPath);
        Set<Cell> bestSpots = new HashSet<>(Set.copyOf(originalPathSet));

        findBestSpots(map, cells, originalPathSet, originalPath, bestSpots);

        System.out.println("Part 2: " + bestSpots.size());
        printAllPath(map, originalPath, start, end, bestSpots);
    }

    public static void findBestSpots(char[][] map, Cell[][] cells, Set<Cell> pathSet, List<Cell> originalPath, Set<Cell> bestSpots) {
        for (var cell : originalPath) {
            List<List<Cell>> allAlternatePaths = bfs(map, cells, new Cell(cell.x, cell.y), pathSet, originalPath);

            for (var altPath : allAlternatePaths) {
                bestSpots.addAll(altPath);
            }
        }
    }

    public static List<List<Cell>> bfs(char[][] map, Cell[][] cells, Cell from, Set<Cell> originalPathSet, List<Cell> originalPath) {
        if (getNeighbours(map, from).stream().map(c -> new Cell(c[0], c[1])).filter(c -> !originalPathSet.contains(c)).count() == 0) {
            return Collections.emptyList();
        }

        List<List<Cell>> allAlternatePaths = new ArrayList<>();

        Queue<Cell> queue = new ArrayDeque<>();
        queue.add(from);

        Set<Cell> visited = new HashSet<>();
        Set<Cell> notAllowed = new HashSet<>(getNeighbours(map, from).stream().map(c -> new Cell(c[0], c[1])).filter(originalPathSet::contains).toList());
        Map<Cell, List<Cell>> paths = new HashMap<>();
        paths.put(from, List.of(from));
        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            if (visited.contains(cur)) {
                continue;
            }
            visited.add(cur);

            for (var c : getNeighbours(map, cur).stream().map(c -> new Cell(c[0], c[1])).filter(c -> !notAllowed.contains(c) && !visited.contains(c)).toList()) {
                if (originalPathSet.contains(c)) {
                    if (from.x == 11 && from.y == 3 && c.x == 7 && c.y == 5) {
                        System.out.println(paths.get(cur));
                    }

                    List<Cell> alternativePath = comparePaths(cells, from, c, List.copyOf(paths.get(cur)), originalPath);
                    if (!alternativePath.isEmpty()) {
                        allAlternatePaths.add(alternativePath);
                    }
                }

                queue.add(c);
                List<Cell> newPath = new ArrayList<>(List.copyOf(paths.get(cur)));
                newPath.add(c);
                paths.put(c, newPath);
            }
        }

        return allAlternatePaths;
    }

    public static Cell[][] dijkstra(char[][] map, Cell from) {
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

        return cells;
    }

    public static List<Cell> comparePaths(Cell[][] cells, Cell start, Cell neighbour, List<Cell> subPathSoFar, List<Cell> originalPath) {
        Cell s, e;
        if (cells[start.x][start.y].cost > cells[neighbour.x][neighbour.y].cost) {
            e = cells[start.x][start.y];
            s = cells[neighbour.x][neighbour.y];
        } else {
            e = cells[neighbour.x][neighbour.y];
            s = cells[start.x][start.y];
        }

        int startIndex = originalPath.indexOf(s);
        int endIndex = originalPath.indexOf(e);
        assert startIndex != -1;
        assert endIndex != -1;

        List<Cell> alternativePath = new ArrayList<>(subPathSoFar);
        alternativePath.add(new Cell(neighbour.x, neighbour.y));
        if (s.equals(neighbour)) {
            alternativePath = alternativePath.reversed();
        }
        updateAlternateRoute(alternativePath, s.withDir, s.from);

        List<Cell> before = List.copyOf(originalPath).subList(0, startIndex);
        List<Cell> after = List.copyOf(originalPath).subList(endIndex + 1, originalPath.size());
        List<Cell> alternatedFullPath = Stream.concat(Stream.concat(before.stream(), alternativePath.stream()), after.stream()).toList();

        int[] originalCost = count(originalPath);
        int[] alternativeCost = count(alternatedFullPath);
        if (originalCost[0] == alternativeCost[0] && originalCost[1] == alternativeCost[1]) {
            return alternativePath;
        }
        return Collections.emptyList();
    }

    public static void updateAlternateRoute(List<Cell> alternateSubRoute, int startDir, Cell startFrom) {
        alternateSubRoute.getFirst().withDir = startDir;
        alternateSubRoute.getFirst().from = startFrom;


        for (int i = 1; i < alternateSubRoute.size(); i++) {
            Cell prev = alternateSubRoute.get(i - 1);
            Cell cur = alternateSubRoute.get(i);

            int dir = getNeighbourDir(prev, cur);
            assert dir != -1;

            cur.withDir = dir;
            cur.from = prev;
        }
    }

    public static int[] count(List<Cell> path) {
        int[] cost = new int[]{path.size(), 0}; // [length, numberOfTurns]
        int numberOfTurns = 0;
        for (int i = 1; i < path.size(); i++) {
            Cell prev = path.get(i - 1);
            Cell cur = path.get(i);

            if (prev.withDir != cur.withDir) numberOfTurns++;
        }
        cost[1] = numberOfTurns;
        return cost;
    }

    public static int getNeighbourDir(Cell c, Cell n) {
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int x = c.x + DIRECTIONS[i][0];
            int y = c.y + DIRECTIONS[i][1];

            if (n.x == x && n.y == y) return i;
        }

        return -1;
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

    public static void printAllPath(char[][] map, List<Cell> path, Cell start, Cell end, Set<Cell> alternates) {
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
                } else if (alternates.contains(new Cell(i, j))) {
                    System.out.print("○");
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