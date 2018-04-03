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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 *class controller for working with a regForm form
 *@author Dmytro Cherevko
 *@version 1.0
 */

public class RegController {

    private final static Logger logger = Logger.getLogger(RegController.class);
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

    /**
     * Called to initialize a controller after its root element has been completely processed.
     */
    @FXML
    private void initialize() {
        listener = ServerListener.getListener();
        listener.setRegController(this);
    }

    /**
     * method for processing keystrokes regButton
     * @param event regButton is pressed
     */
    @FXML
    private void pressRegButton(ActionEvent event) {
        if (isValidUserInfo()) {
            initializeUserInfo();
            try {
                if (isValidUserInfo()) {
                    listener.getOutClientXML().send("REG", username, password);
                }
            } catch (XMLStreamException e) {
                logger.error("Can not send message with key - REG", e);
            }
        }
    }

    /**
     * method for processing keystrokes signButton
     * @param event press the button
     */
    @FXML
    private void pressSignButton(ActionEvent event) {
        if (isValidUserInfo()) {
            initializeUserInfo();
            try {
                if (isValidUserInfo()) {
                    listener.getOutClientXML().send("LOG IN", username, password);
                    listener.setUsername(username);
                }
            } catch (XMLStreamException e) {
                logger.error("Can not send message with key - LOG IN", e);
            }
        }
    }

    /**
     * method for creating a common application window
     * @param isAdmin set the user status
     */
    protected void showCommonWindow(boolean isAdmin) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = new Stage();
                Parent root = null;
                comWindow = stage;
                try {
                    root = FXMLLoader.load(getClass().getResource(MainLauncher.getPropertyForms().getProperty("commonWindow")));
                    logger.info("Load commonWindow.fxml is successfully");
                } catch (IOException e) {
                    logger.error("Can not load commonWindow.fxml", e);
                }
                if (root != null) {
                    stage.setTitle("Sea Battle 2018");
                    Scene scene = new Scene(root, 640, 360 + (isAdmin ? 40 : 0));
                    stage.setScene(scene);
                    stage.setResizable(false);
                    clearUserInput();
                    stage.setOnCloseRequest((WindowEvent e) -> {
                        try {
                            if (GameController.getGameController() != null && CommonWindowController.getCwController().getGameWindow() != null){
                                CommonWindowController.getCwController().getGameWindow().hide();
                                if (!GameController.getGameController().isGameFinish())
                                    listener.getOutClientXML().send("SURRENDER", username);
                            }
                            listener.getOutClientXML().send("LOG OUT", username);
                        } catch (XMLStreamException e1) {
                            logger.error("Can not send message with key - LOG OUT or SURRENDER", e1);
                        } finally {
                            MainLauncher.getPrimaryStageObj().show();
                            comWindow.hide();
                        }
                    });
                    stage.show();
                    if (!isAdmin) {
                        CommonWindowController.getCwController().getAdminBox().setVisible(false);
                    }
                    MainLauncher.getPrimaryStageObj().hide();
                }
            }
        });
    }

    /**
     * Method for verifying the correctness of the entered server data
     * @return true if valid info
     */
    private boolean isValidServerInfo() {
        port = -1;
        try {
            port = Integer.parseInt(txtServerPort.getText());
        } catch (NumberFormatException e) {
            DialogManager.showInfoDialog(listener.getCurrentWindow(), "Server port", "No valid port");
        }
        if (txtServerPort.getText().isEmpty()) {
            DialogManager.showInfoDialog(listener.getCurrentWindow(), "Server port", "Please,enter the server port");
            return false;
        }
        if (port < 0 || port > 65535) {
            DialogManager.showInfoDialog(listener.getCurrentWindow(), "Port info", "Please, enter the valid port (0...65535)");
            return false;
        }
        return true;
    }

    /**
     * method for verifying the correctness of the entered user data
     * @return true if valid info
     */
    private boolean isValidUserInfo() {
        if (loginField.getText().isEmpty() || loginField.getText().length() > 12) {
            DialogManager.showInfoDialog(listener.getCurrentWindow(), "Login info", "Please, enter the login from 1 symbol to 12");
            loginField.setText("");
            return false;
        }
        if (loginField.getText().contains(" ")) {
            DialogManager.showInfoDialog(listener.getCurrentWindow(), "Login info", "Login can not include the space");
            loginField.setText("");
            return false;
        }
        if (passField.getText().isEmpty()) {
            DialogManager.showInfoDialog(listener.getCurrentWindow(), "Password info", "Please,enter the password");
            return false;
        }
        if (txtServerHostname.getText().isEmpty()) {
            DialogManager.showInfoDialog(listener.getCurrentWindow(), "Host info", "Please,enter the server host");
            return false;
        }
        return true;
    }

    /**
     * method for initializing information about the server (host, port)
     */
    private void initializeServerInfo() {
        hostname = txtServerHostname.getText();
        port = Integer.parseInt(txtServerPort.getText());
    }

    /**
     * method for initializing user information (username, password)
     */
    private void initializeUserInfo() {
        username = loginField.getText();
        password = passField.getText();
    }

    /**
     * method for cleaning fields loginField and passField
     */
    private void clearUserInput() {
        loginField.setText("");
        passField.setText("");
    }

    /**
     * method for handling the button click event (btnConnect)
     * @param event press the button
     */
    public void pressConnectBtn(ActionEvent event) {
        if (listener.isConnect()) {
            listener.disconnect();
        } else if (isValidServerInfo()) {
            initializeServerInfo();
            listener.connect(hostname, port);
        }
    }

    public static RegController getRegController() {
        return regController;
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