package seaBattle.model;

/**
 * model of ship
 * @author Roman Kraskovskiy
 */
public class Ship {

    private int [] ship = new int[4];
    private int health;
    private boolean vertical;

    /**
     * constructor
     * @param ship array of coordinates
     */
    public Ship(int[] ship) {
        this.ship = ship;
        if (ship[0] == ship[2]) {
            health = ship[3] - ship[1]+1;
            vertical = true;
        }else {
            health = ship[2] - ship[0]+1;
            vertical = false;
        }
    }

    /**
     * @return array of coordinates
     */
    public int[] getShip() {
        return ship;
    }

    /**
     * @return length of ship
     */
    public int getHealth() {
        return health;
    }

    /**
     * @return true if ship is vertical
     */
    public boolean isVertical() {
        return vertical;
    }
}
