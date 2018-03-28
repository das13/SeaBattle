package client.controller.models;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *class extends StackPane for creating cells for Game Fields (field designations)
 *@author Dmytro Cherevko
 *@version 1.0
 */

public class FieldDesignation extends StackPane{
        private static final int TILE_SIZE = 25;

        public FieldDesignation(String s, int y, int x) {
            Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
            Text text = new Text();
            border.setStroke(Color.LIGHTSEAGREEN);
            border.setFill(Color.BEIGE);
            text.setFont(Font.font(12));
            text.setFill(Color.GRAY);
            text.setText(s);
            getChildren().addAll(border, text);
            setTranslateX(x * TILE_SIZE);
            setTranslateY(y * TILE_SIZE);
        }
}
