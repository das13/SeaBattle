package client.controller;

import client.MainLauncher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class RegController {
    final static Logger logger = Logger.getLogger(ServerListener.class);

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
    public static CommonWindowController cwController;
    private Scene scene;
    private String hostname;
    private String username;
    private String password;
    private int port;
    private Thread serverListenerThread;
    private ServerListener listener;

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

    public static RegController getRegController() {
        return regController;
    }

    private String status = "Input status: ";

    @FXML
    private void pressRegButton(ActionEvent event){
        if(isNotEmptyFields()){
            checkStatus();
            initializeUserInput();
            listener = new ServerListener(hostname, port, username, password,  "REG");
        }
    }

    @FXML
    private void pressSignButton(ActionEvent event) throws IOException {
        if(isNotEmptyFields()){
            checkStatus();
            initializeUserInput();
            listener = new ServerListener(hostname, port, username, password,  "LOG IN");
        }
    }
    protected void showCommonWindow() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/commonWindow.fxml"));
                logger.info("Load commonWindow.fxml is successfully");
            } catch (IOException e) {
                logger.info("Can not load commonWindow.fxml", e);
                logger.error("Can not load commonWindow.fxml", e);
            }
            stage.setOnCloseRequest((WindowEvent e) -> {

                try {
                    System.out.println("OUT" + ServerListener.getOutClientXML() == null);
                    System.out.println("key: LOG OUT " + "value " + username);
                    listener.getOutClientXML().send("LOG OUT", username);
                    listener.setIsConnect(false);
                } catch (XMLStreamException e1) {
                    logger.error("Logout error", e1);
                }
                MainLauncher.getPrimaryStageObj().show();

                /*
                try {
                    ServerListener.getOutClientXML().send("LOGOUT", username);
                    serverListenerThread = null;
                } catch (XMLStreamException e1) {
                    logger.error("Logout error", e1);
                }
                */
            });
            stage.setTitle("Sea Battle 2018");
            Scene scene = new Scene(root,640,360);
            stage.setScene(scene);
            stage.setMinHeight(360);
            stage.setMinWidth(640);
            stage.show();
            clearUserInput();
            MainLauncher.getPrimaryStageObj().hide();

        });
    }

    private boolean isNotEmptyFields(){
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
        if(txtServerPort.getText().isEmpty()) {
            inputStatus.setText(status + "Please,enter the server port");
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

    private void initializeUserInput() {
        hostname = txtServerHostname.getText();
        port = Integer.parseInt(txtServerPort.getText());
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

    public void showErrorDialog(String message) {
        Platform.runLater(()-> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText(message);
            alert.setContentText("Please check server info and try again.");
            alert.showAndWait();
        });

    }
}
