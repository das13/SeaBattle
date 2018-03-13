package client.controller;

import client.MainLauncher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class RegController{

    final static Logger logger = Logger.getLogger(RegController.class);

    @FXML
    public Button btnConnect;
    @FXML
    private PasswordField passField;
    @FXML
    private TextField loginField;
    @FXML
    private TextField txtServerPort;
    @FXML
    private TextField txtServerHostname;
    @FXML
    private Button regButton;
    @FXML
    private Button signButton;
    @FXML
    private Label inputStatus;
    private String hostname;
    private String username;
    private String password;
    private int port;
    private static ServerListener listener;

    public void setInputStatus(Label inputStatus) {
        this.inputStatus = inputStatus;
    }

    public Label getInputStatus() {
        return inputStatus;
    }

    private static RegController regController;

    public RegController() {
        regController = this;
    }

    public Stage comWindow;

    @FXML
    private void initialize(){

    }

    public static RegController getRegController() {
        return regController;
    }

    private String status = "Input status: ";

    @FXML
    private void pressRegButton(ActionEvent event) {
            if(isValidUserInfo()){
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            checkStatus();
                            initializeUserInfo();
                            if (isValidUserInfo()){
                                ServerListener.getListener().getOutClientXML().send("REG", username, password);
                            }
                        } catch (XMLStreamException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }


    @FXML
    private void pressSignButton(ActionEvent event) {
        if(isValidUserInfo()){
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        checkStatus();
                        initializeUserInfo();
                        if (isValidUserInfo()){
                            ServerListener.getListener().getOutClientXML().send("LOG IN", username, password);
                            ServerListener.getListener().setUsername(username);
                        }
                    } catch (XMLStreamException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    protected void showCommonWindow() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = new Stage();
                Parent root = null;
                comWindow = stage;
                try {
                    root = FXMLLoader.load(getClass().getResource("/views/commonWindow.fxml"));
                    logger.info("Load commonWindow.fxml is successfully");
                } catch (IOException e) {
                    logger.info("Can not load commonWindow.fxml", e);
                    logger.error("Can not load commonWindow.fxml", e);
                }
                stage.setOnCloseRequest((WindowEvent e) -> {
                    try {
                        ServerListener.getListener().getOutClientXML().send("LOG OUT", username);

                    } catch (XMLStreamException e1) {
                        logger.error("Logout error", e1);
                    } finally {
                        MainLauncher.getPrimaryStageObj().show();
                        comWindow.hide();
                    }
                    //listener.disconnect();

                });
                stage.setTitle("Sea Battle 2018");
                Scene scene = new Scene(root,640,360);
                stage.setScene(scene);
                stage.setMinHeight(360);
                stage.setMinWidth(640);
                stage.show();
                clearUserInput();
                MainLauncher.getPrimaryStageObj().hide();
            }
        });
    }


    private boolean isValidServerInfo() {
        port = -1;
        try {
            port = Integer.parseInt(txtServerPort.getText());
        } catch (NumberFormatException e) {
            logger.info("No valid port",e);
        }
        if(txtServerPort.getText().isEmpty()) {
            inputStatus.setText(status + "Please,enter the server port");
            return false;
        }
        if(port < 0 || port > 65535) {
            inputStatus.setText(status + "Please,enter the valid port (0...65535)");
            return false;
        }
        return true;
    }

    private boolean isValidUserInfo(){

        if(loginField.getText().isEmpty()) {
            inputStatus.setText(status + "Please, enter the login");
            return false;
        }
        if(loginField.getText().contains(" ")) {
            inputStatus.setText(status + "Login can not include the space");
            return false;
        }
        if(passField.getText().isEmpty()) {
            inputStatus.setText(status + "Please,enter the password");
            return false;
        }
        if(txtServerHostname.getText().isEmpty()) {
            inputStatus.setText(status + "Please,enter the server host");
            return false;
        }
        return true;
    }

    private void checkStatus(){
        inputStatus.setText(status + "Cheking your input...");
    }

    private void initializeServerInfo(){
        hostname = txtServerHostname.getText();
        port = Integer.parseInt(txtServerPort.getText());
    }

    private void initializeUserInfo() {
        username = loginField.getText();
        password = passField.getText();
    }

    private void clearUserInput(){
        loginField.setText("");
        passField.setText("");
    }

    public void closeSystem(){
        Platform.exit();
        System.exit(0);
    }

    public void pressConnectBtn(ActionEvent event) {
        if (isValidServerInfo() ){
            initializeServerInfo();
            ServerListener.getListener().connect(hostname, port);
        }
    }

    public String getUsername() {
        return username;
    }

    public Button getRegButton() {
        return regButton;
    }

    public Button getSignButton() {
        return signButton;
    }

    public Button getBtnConnect() {
        return btnConnect;
    }

    public Stage getComWindow() {
        return comWindow;
    }

    public void hideCommonWindow() {
        comWindow.hide();
    }
}