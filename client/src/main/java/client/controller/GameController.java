package client.controller;

import client.controller.models.Cell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

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


}
