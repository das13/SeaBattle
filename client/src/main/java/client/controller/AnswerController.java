package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import javax.xml.stream.XMLStreamException;

public class AnswerController {
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lblStatus;

    private static AnswerController answerController;
    public AnswerController() {
        answerController = this;
    }

    public static AnswerController getAnswerController() {
        return answerController;
    }
    @FXML
    public void initialize(){
        lblStatus.setText("You are attacked from " + ServerListener.getEnemy());
        progressBar.setProgress(0.0);
        Thread th = new Thread(new ProgressBarTh());
    }

    public void btnAcceptPressed(ActionEvent event) {
        try {
            CommonWindowController.getCwController().hideWaitAnswerWindow();
            ServerListener.getOutClientXML().send("REPLY","OK");
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void btnIgnorePressed(ActionEvent event) {
        try {
            CommonWindowController.getCwController().hideWaitAnswerWindow();
            ServerListener.getOutClientXML().send("REPLY","CANCEL");
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
    public Label getLblStatus() {
        return lblStatus;
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
            try {
                ServerListener.getOutClientXML().send("REPLY","CANCEL");
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }
}
