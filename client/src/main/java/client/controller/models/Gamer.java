package client.controller.models;


/**
 *class for creating gamers
 *@autor Dmytro Cherevko
 *@version 1.0
 */

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

    public int getRank() {
        return rank;
    }
}