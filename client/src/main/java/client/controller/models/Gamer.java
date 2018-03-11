package client.controller.models;

public class Gamer {
    String name;
    int rank;

    public Gamer(String name, int rank) {
        this.name = name;
        this.rank = rank;
    }

    public Gamer (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}