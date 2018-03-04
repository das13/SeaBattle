package seaBattle.model;

public class Ship {

    private int [] ship = new int[4];
    private int health;
    private boolean vertical;

    public Ship(int[] ship) {
        this.ship = ship;
        if (ship[0] == ship[2]) {
            health = ship[3] - ship[1];
            vertical = true;
        }else {
            health = ship[2] - ship[0];
            vertical = false;
        }
    }

    public int[] getShip() {
        return ship;
    }

    public void setShip(int[] ship) {
        this.ship = ship;
    }

    public int getHealth() {
        return health;
    }

    public boolean isVertical() {
        return vertical;
    }
}
