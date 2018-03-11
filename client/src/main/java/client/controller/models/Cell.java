package client.controller.models;

import client.controller.GameController;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends StackPane {
    public static final int TILE_SIZE = 25;
    public int x, y;


    public Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        border.setStroke(Color.LIGHTSEAGREEN);
        border.setFill(Color.BEIGE);
        getChildren().add(border);
        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}