package seaBattle.model.testModel;

public class Field {

    Player player;
    Ship p1S4_1 = new Ship(4);
    Ship p1s3_1 = new Ship(3);
    Ship p1s3_2 = new Ship(3);
    Ship p1s2_1 = new Ship(2);
    Ship p1s2_2 = new Ship(2);
    Ship p1s2_3 = new Ship(2);
    Ship p1s1_1 = new Ship(1);
    Ship p1s1_2 = new Ship(1);
    Ship p1s1_3 = new Ship(1);
    Ship p1s1_4 = new Ship(1);

    public Field() {
        for (int i = 0; i < battleField.length; i++) {
            for (int j = 0; j < battleField[i].length; j++) {
                battleField[i][j] = new Sector(i, j);
            }
        }
    }

    private Sector[][] battleField = new Sector[10][10];

    public boolean placeShip(Ship ship) {
        if (ship.getX1() != ship.getX2() && ship.getY1() != ship.getY2()) {
            System.out.println("Wrong ship placement");
            return false;
        }
        for (int i = ship.getX1(); i <= ship.getX2(); i++) {
            for (int j = ship.getY1(); j <= ship.getY2(); j++) {
                if (battleField[j][i].hasShip()) {
                    System.out.println("Sector already busy");
                    return false;
                }
            }
        }

        for (int i = ship.getX1(); i <= ship.getX2(); i++) {
            for (int j = ship.getY1(); j <= ship.getY2(); j++) {
                battleField[j][i].setShip(ship);
            }
        }
        return true;
    }

    //вывод в консоль
    public void print() {
        for (int i = 0; i < battleField.length; i++) {
            for (int j = 0; j < battleField[i].length; j++) {
                System.out.print(battleField[i][j].getInfo());
            }
            System.out.println();
        }
    }

    //getters & setters
    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player1) {
        this.player = player1;
        player.setField(this);
    }

}