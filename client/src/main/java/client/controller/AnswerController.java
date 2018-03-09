package client.controller;

import client.controller.utils.ProgressAnimation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import javax.xml.stream.XMLStreamException;

public class AnswerController {
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lblStatus;
    private ServerListener listener;
    private static AnswerController answerController;
    public AnswerController() {
        answerController = this;
    }

    public static AnswerController getAnswerController() {
        return answerController;
    }
    @FXML
    public void initialize(){
        listener = ServerListener.getListener();
        lblStatus.setText("You are attacked from " + ServerListener.getListener().getEnemy());
        ProgressAnimation progressAnimation = new ProgressAnimation();

        progressBar.setProgress(0.0);
        progressBar.progressProperty().bind(progressAnimation.progressProperty());

        new Thread(progressAnimation).start();
        //new Thread(new ProgressBarTh());

    }

    public void btnAcceptPressed(ActionEvent event) {
        try {
            CommonWindowController.getCwController().hideWaitAnswerWindow();
            ServerListener.getListener().getOutClientXML().send("REPLY", ServerListener.getListener().getEnemy(),
                    ServerListener.getListener().getUsername());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void btnIgnorePressed(ActionEvent event) {

        CommonWindowController.getCwController().hideWaitAnswerWindow();
        //ServerListener.getListener().getOutClientXML().send("REPLY","CANCEL");

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
                listener.getOutClientXML().send("REPLY","CANCEL");
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }
}
