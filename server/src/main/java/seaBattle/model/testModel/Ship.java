package seaBattle.model.testModel;

public class Ship {

    private Player player;
    private int health;
    private Sector sector;
    private int size;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int [][] ship = new int[2][2];

    public Ship(int size) {
        this.size = size;
    }

    //(x - буквы в оригинальной игре, y - цифры)
    public Ship(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    //getters & setters
    public int[][] getShip() {
        return ship;
    }
    public void setShip(int[][] ship) {
        this.ship = ship;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    public int getX1() {
        return x1;
    }
    public void setX1(int x1) {
        this.x1 = x1;
    }
    public int getY1() {
        return y1;
    }
    public void setY1(int y1) {
        this.y1 = y1;
    }
    public int getX2() {
        return x2;
    }
    public void setX2(int x2) {
        this.x2 = x2;
    }
    public int getY2() {
        return y2;
    }
    public void setY2(int y2) {
        this.y2 = y2;
    }

    public Sector getSector() {
        return sector;
    }
    public void setSector(Sector sector) {
        this.sector = sector;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
}