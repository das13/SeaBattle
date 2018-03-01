package client.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameController {

    @FXML
    private GridPane userPane;

    @FXML
    private GridPane enemyPane;

    @FXML
    private Label lblStatus;


    @FXML
    private void initialize(){

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                userPane.add(new Cell(true,i,j), i, j);
            }
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                enemyPane.add(new Cell(false,i,j), i, j);
            }
        }
    }

    public class Cell extends Rectangle{
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
}
