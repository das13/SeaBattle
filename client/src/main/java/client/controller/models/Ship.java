package client.controller.models;

import client.controller.GameController;
import javafx.scene.paint.Color;

public class Ship {


    private int x, y, length, position;


    public Ship(int x, int y, int length, int position) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.position = position;
    }

    public void setShip() {
        for (int i = 0; i < length; i++) {
            Cell cell = new Cell(x + i * ((position == 1)?0:1), y + i * ((position == 1)?1:0));
            cell.border.setFill(Color.LIGHTGRAY);
            GameController.getGameController().getUserPane().getChildren().add(cell);
        }
    }
}
