package seaBattle.model.testModel;

public class LaunchTestModel {

    public static void main(String[] args) {

        Player p1 = new Player("1");
        Player p2 = new Player("2");
        Game game = new Game(p1, p2);

        p1.getField().placeShip(new Ship(0, 8, 3, 8));
        p1.getField().placeShip(new Ship(7, 2, 7, 4));
        p1.getField().placeShip(new Ship(3,1,3,1));
        p1.getField().print();

    }
}
