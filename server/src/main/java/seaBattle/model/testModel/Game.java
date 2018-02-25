package seaBattle.model.testModel;

public class Game {

    private Field fieldP1 = new Field();
    private Field fieldP2 = new Field();
    private Player p1;
    private Player p2;

    public Game(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
        fieldP1.setPlayer(p1);
        fieldP2.setPlayer(p2);
    }

    //getters & setters
    public Field getFieldP1() {
        return fieldP1;
    }
    public void setFieldP1(Field fieldP1) {
        this.fieldP1 = fieldP1;
    }
    public Field getFieldP2() {
        return fieldP2;
    }
    public void setFieldP2(Field fieldP2) {
        this.fieldP2 = fieldP2;
    }
    public Player getP1() {
        return p1;
    }
    public void setP1(Player p1) {
        this.p1 = p1;
    }
    public Player getP2() {
        return p2;
    }
    public void setP2(Player p2) {
        this.p2 = p2;
    }
}
