package client.controller;

import client.controller.utils.ProgressAnimation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
/**
 *class controller for working with a WaitAnswer form
 *@autor Dmytro Cherevko
 *@version 1.0
 */

public class WaitAnswerController {
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lblStatus;
    private static WaitAnswerController waitAnswerController;
    public WaitAnswerController() {
        waitAnswerController = this;
    }

    @FXML
    private void initialize(){
        lblStatus.setText("Waiting for answer from " + CommonWindowController.getCwController().getEnemy());
        ProgressAnimation progressAnimation = new ProgressAnimation();
        progressBar.setProgress(0.0);
        progressBar.progressProperty().bind(progressAnimation.progressProperty());
        new Thread(progressAnimation).start();
    }
}
