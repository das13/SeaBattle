package client.controller.models;

import client.controller.GameController;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends StackPane {
    public static final int TILE_SIZE = 25;
    private int x, y;
    private boolean isEnemy;

    public Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);

    public Cell(int x, int y, boolean isEnemy) {
        this.x = x;
        this.y = y;
        this.isEnemy = isEnemy;
        border.setStroke(Color.LIGHTSEAGREEN);
        border.setFill(Color.BEIGE);
        getChildren().add(border);
        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);
        setOnMouseClicked(e -> GameController.getCwController().sendAnswer(x, y));
    }

/*    public void set() {
        if (!isEnemy) {
            int length = GameController.getCwController().getLength();
            int position = GameController.getCwController().getPosition();
            System.out.println("x = " + x + " y = " + y + " length = " + length + " position = " +
                    (position == 0 ? "horizontal" : "vertical"));

            Ship ship = new Ship(x, y, length, position);
            ship.setShip();
        }else{
            System.out.println("Enemy's field");
        }

    }*/
}