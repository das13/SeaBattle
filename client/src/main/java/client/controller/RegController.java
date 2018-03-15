package client.controller;

import client.MainLauncher;
import client.controller.utils.DialogManager;
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
    private String hostname;
    private String username;
    private String password;
    private int port;
    private static ServerListener listener;
    private static RegController regController;
    private Stage comWindow;

    public RegController() {
        regController = this;
    }

    @FXML
    private void initialize(){
        listener = ServerListener.getListener();
        listener.setRegController(this);
    }

    public static RegController getRegController() {
        return regController;
    }

    @FXML
    private void pressRegButton(ActionEvent event) {
            if(isValidUserInfo()){
                initializeUserInfo();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isValidUserInfo()){
                                listener.getOutClientXML().send("REG", username, password);
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
            initializeUserInfo();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (isValidUserInfo()){
                            listener.getOutClientXML().send("LOG IN", username, password);
                            listener.setUsername(username);
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
                        listener.getOutClientXML().send("LOG OUT", username);
                    } catch (XMLStreamException e1) {
                        logger.error("Logout error", e1);
                    } finally {
                        MainLauncher.getPrimaryStageObj().show();
                        comWindow.hide();
                    }

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
            DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(),"Server port","No valid port");
        }
        if(txtServerPort.getText().isEmpty()) {
            DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(),"Server port","Please,enter the server port");
            return false;
        }
        if(port < 0 || port > 65535) {
            DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(),"Port info", "Please, enter the valid port (0...65535)");
            return false;
        }
        return true;
    }

    private boolean isValidUserInfo(){

        if(loginField.getText().isEmpty()) {
            DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(),"Login info","Please, enter the login");
            return false;
        }
        if(loginField.getText().contains(" ")) {
            DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(),"Login info","Login can not include the space");
            return false;
        }
        if(passField.getText().isEmpty()) {
            DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(),"Password info","Please,enter the password");
            return false;
        }
        if(txtServerHostname.getText().isEmpty()) {
            DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(),"Host info","Please,enter the server host");
            return false;
        }
        return true;
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

    public void pressConnectBtn(ActionEvent event) {
        if (listener.isConnect()){
            listener.disconnect();
        } else if (isValidServerInfo() ){
            initializeServerInfo();
            listener.connect(hostname, port);
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