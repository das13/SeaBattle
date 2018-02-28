package client.controller;

public class Gamer {
    String name;
    int wins;
    int loses;

    public Gamer(String name, int wins, int loses) {
        this.name = name;
        this.wins = wins;
        this.loses = loses;
    }

    public String getName() {
        return name;
    }

    public int getWins() {
        return wins;
    }

    public int getLoses() {
        return loses;
    }
}