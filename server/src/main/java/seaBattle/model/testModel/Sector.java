package seaBattle.model.testModel;

public class Sector {

    private Ship ship;
    private int x;
    private int y;

    public Sector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //getters & setters
    public Ship getShip() {
        return ship;
    }
    public void setShip(Ship ship) {
        this.ship = ship;
    }
    public String getInfo() {
        return ship == null? "-" : "X";
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public boolean hasShip() {
        return ship != null ;
    }
}
