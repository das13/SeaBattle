package com.cherevko.dmytro.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class RegController {

    private MainController mainController ;

    public void setMainController(MainController mainController) {
        this.mainController = mainController ;
    }

    @FXML
    private PasswordField passField;
    @FXML
    private TextField loginField;
    @FXML
    private Button regButton;
    @FXML
    private Button signButton;
    @FXML
    private Label inputStatus;

    private String status = "Input status: ";

    @FXML
    private void pressRegButton(ActionEvent event){
        if(isNotEmptyFields()){
            checkStatus();
            showCommonWindow(event);
            deleteRegData();
        }
    }

    @FXML
    private void pressSignButton(ActionEvent event) {
        if(isNotEmptyFields()){
            checkStatus();
            showCommonWindow(event);
            deleteRegData();
        }
    }

    private void deleteRegData(){
        passField.setText("");
        loginField.setText("");
        inputStatus.setText(status);
    }

    private boolean isNotEmptyFields(){
        if(loginField.getText().isEmpty()) {
            inputStatus.setText(status + "Please, enter the login");
            return false;
        }

        if(passField.getText().isEmpty()) {
            inputStatus.setText(status + "Please,enter the password");
            return false;
        }
        return true;
    }

    private void checkStatus(){
        inputStatus.setText(status + "Cheking your input...");
    }

    private void showCommonWindow(ActionEvent event) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../view/FXMLForms/commonWindow.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Sea Battle 2018");
        Scene scene = new Scene(root,640,360);
        //scene.getStylesheets().add(0, "resources/css/main.css");
        stage.setScene(scene);
        stage.setMinHeight(360);
        stage.setMinWidth(640);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        stage.show();
    }
}
