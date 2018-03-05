package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;


import javax.xml.stream.XMLStreamException;

public class WaitAnswerController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label lblStatus;

    private String enemyName;

    private static WaitAnswerController waitAnswerController;

    public WaitAnswerController() {
        waitAnswerController = this;
    }

    public static WaitAnswerController getWaitAnswerController() {
        return waitAnswerController;
    }

    @FXML
    private void initialize(){
        lblStatus.setText("Waiting for answer from " + CommonWindowController.getEnemy());
        progressBar.setProgress(0.0);
        Thread th = new Thread(new ProgressBarTh());
    }



    class ProgressBarTh implements Runnable {
        public ProgressBarTh(){
            new Thread(this).start();
        }
        @Override
        public void run(){
            for (int i = 0; i <= 100; i++) {
                progressBar.setProgress(i/100.0);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
