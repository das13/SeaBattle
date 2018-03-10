package client.controller.models;

import client.controller.GameController;
import javafx.scene.paint.Color;

public class Ship {

    //private static int ships[] = new int[]{1,1,1,1,2,2,2,3,3,4};
    private int x, y, x2, y2, length, position;


    public Ship(int x, int y, int length, int position) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.position = position;
    }

/*
    public Ship(int x, int y, int x2, int y2) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;

    }
*/

    public void setShip() {
        for (int i = 0; i < length; i++) {
            Cell cell = new Cell(x + i * ((position == 1)?0:1), y + i * ((position == 1)?1:0), false);
            cell.border.setFill(Color.LIGHTGRAY);
            GameController.getGameController().getUserPane().getChildren().add(cell);
        }
    }
}
