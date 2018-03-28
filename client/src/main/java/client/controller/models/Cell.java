package client.controller.models;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *class extends StackPane for creating cells for Game Fields
 *@author Dmytro Cherevko
 *@version 1.0
 */

public class Cell extends StackPane {
    private static final int TILE_SIZE = 25;
    private int x, y;
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