package client.controller;

import client.controller.utils.ProgressAnimation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import javax.xml.stream.XMLStreamException;
/**
 *class controller for working with a Answer form
 *@autor Dmytro Cherevko
 *@version 1.0
 */
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
    public void initialize() {
        listener = ServerListener.getListener();
        lblStatus.setText("You are attacked from " + ServerListener.getListener().getEnemy());
        ProgressAnimation progressAnimation = new ProgressAnimation();
        progressBar.setProgress(0.0);
        progressBar.progressProperty().bind(progressAnimation.progressProperty());
        new Thread(progressAnimation).start();
    }

    public void btnAcceptPressed(ActionEvent event) {
        try {
            CommonWindowController.getCwController().hideWaitAnswerWindow();
            listener.getOutClientXML().send("REPLY", listener.getEnemy(), listener.getUsername());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void btnIgnorePressed(ActionEvent event) {
        CommonWindowController.getCwController().hideWaitAnswerWindow();
    }
}
