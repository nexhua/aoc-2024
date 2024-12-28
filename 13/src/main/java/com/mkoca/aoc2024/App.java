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
    public int tokenCount;

    public Game(Coin a, Coin b, Coin target) {
        this.a = a;
        this.b = b;
        this.target = target;
        this.tokenCount = 0;
    }

    @Override
    public String toString() {
        return String.format("A: %s\nB: %s\nPrize: %s\n", a, b, target);
    }
}

class Coin {
    public int x;
    public int y;
    public int cost;

    public Coin(int x, int y, int cost) {
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
    public static final String FILE_NAME = "input.txt";

    public static void main(String[] args) {
        part1();
        // part2();
    }

    public static void part1() {
        List<Game> games = getGames(readFile());

        for (var game : games) {
            int overshoot = (int) Math.min(Math.floor((double) game.target.x / game.b.x), Math.floor((double) game.target.y / game.b.y));
            for (int bCount = overshoot; bCount > 0; bCount--) {
                for (int aCount = 1; aCount < 101; aCount++) {
                    int xDiff = game.target.x - (bCount * game.b.x + aCount * game.a.x);
                    int yDiff = game.target.y - (bCount * game.b.y + aCount * game.a.y);

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

        System.out.println("Part 1: " + games.stream().map(g -> g.tokenCount).reduce(0, Integer::sum));
    }

    public static void part2() {

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
