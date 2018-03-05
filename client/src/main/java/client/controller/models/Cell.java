package client.controller.models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
    private int x, y;
    private boolean isUser;
    public Cell(boolean isUser, int x, int y) {
        super(19,19);
        this.x = x;
        this.y = y;
        this.isUser = isUser;
        setFill(Color.LIGHTBLUE);
    }
}
