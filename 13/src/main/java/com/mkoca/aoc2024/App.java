package com.mkoca.aoc2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


class Game {
    public Coin a;
    public Coin b;
    public Coin target;
    public long tokenCount;

    public Game(Coin a, Coin b, Coin target) {
        this.a = a;
        this.b = b;
        this.target = target;
        this.tokenCount = 0;
    }

    @Override
    public String toString() {
        return String.format("A: %s\nB: %s\nPrize: %s", a, b, target);
    }
}

class Coin {
    public long x;
    public long y;
    public long cost;

    public Coin(long x, long y, long cost) {
        this.x = x;
        this.y = y;
        this.cost = cost;
    }

    @Override
    public String toString() {
        if (cost != 0) return String.format("Cost=%d X=%d Y=%d", cost, x, y);
        return String.format("   X=%d Y=%d", x, y);
    }
}

public class App {
    public static final String FILE_NAME = "sample.txt";

    public static void main(String[] args) {
        part1();
        part2();
    }

    public static void part1() {
        List<Game> games = getGames(readFile());

        for (var game : games) {
            int overshoot = (int) Math.min(Math.floor((double) game.target.x / game.b.x), Math.floor((double) game.target.y / game.b.y));
            for (int bCount = overshoot; bCount > 0; bCount--) {
                for (int aCount = 1; aCount < 101; aCount++) {
                    long xDiff = game.target.x - (bCount * game.b.x + aCount * game.a.x);
                    long yDiff = game.target.y - (bCount * game.b.y + aCount * game.a.y);

                    if (xDiff < 0 || yDiff < 0) {
                        break;
                    }

                    if (xDiff == 0 && yDiff == 0) {
                        game.tokenCount = aCount * game.a.cost + bCount * game.b.cost;
                        break;
                    }
                }
            }
        }

        System.out.println("Part 1: " + games.stream().map(g -> g.tokenCount).reduce(0L, Long::sum));
    }

    public static void part2() {
        List<Game> games = getGames(readFile());
        for (var game : games) {

            // check if slopes are same, if it is there would be no solution
            double slopeEq1 = -1 * (double) game.a.x / (double) game.b.x;
            double slopeEq2 = -1 * (double) game.a.y / (double) game.b.y;
            if (slopeEq1 == slopeEq2) {
                System.out.println("No solution!");
                game.tokenCount = 0;
                continue;
            }

            game.target.x += 10000000000000L;
            game.target.y += 10000000000000L;

            // equation 1 => A.x * alpha + B.x * beta = T.x
            // equation 2 => A.y * alpha + B.y * beta = T.y
            // solve for beta
            // beta = eq1 / A.x *

            long tt = game.target.y - (game.target.x * game.a.y / game.a.x);
            long beta = tt * game.a.x / (game.a.x * game.b.y - game.b.x * game.a.y);
            long alpha = (game.target.x - game.b.x * beta) / game.a.x;

            System.out.println(game);
            System.out.println("Alpha : " + alpha);
            System.out.println("Beta  : " + beta);

            if ((game.target.x - (game.a.x * alpha + game.b.x * beta) == 0) && (game.target.y - (game.a.y * alpha + game.b.y * beta) == 0)) {
                System.out.println("Solution exists");
                game.tokenCount = alpha * game.a.cost + beta * game.b.cost;
            }
            System.out.println();
        }

        System.out.println("Part 2: " + games.stream().map(g -> g.tokenCount).reduce(0L, Long::sum));
    }

    public static List<Game> getGames(List<String> input) {
        List<Game> games = new ArrayList<>();

        Coin a = null;
        Coin b = null;
        Coin prize = null;

        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);

            if (line.startsWith("Button A")) {
                int sep = line.indexOf(',');
                int x = Integer.parseInt(line.substring(12, sep));
                int y = Integer.parseInt(line.substring(sep + 3));
                a = new Coin(x, y, 3);
            }

            if (line.startsWith("Button B")) {
                int sep = line.indexOf(',');
                int x = Integer.parseInt(line.substring(12, sep));
                int y = Integer.parseInt(line.substring(sep + 3));
                b = new Coin(x, y, 1);
            }

            if (line.startsWith("Prize")) {
                int sep = line.indexOf(',');
                int x = Integer.parseInt(line.substring(9, sep));
                int y = Integer.parseInt(line.substring(sep + 4));
                prize = new Coin(x, y, 0);
            }

            if (a != null && b != null && prize != null) {
                Game game = new Game(a, b, prize);
                games.add(game);
                a = b = prize = null;
            }
        }

        return games;
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
