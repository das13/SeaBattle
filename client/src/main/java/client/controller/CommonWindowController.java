package client.controller;


import client.MainLauncher;
import client.controller.models.Gamer;
import client.controller.utils.DialogManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *class controller for working with a commonWindow form
 *@author Dmytro Cherevko
 *@version 1.0
 */
public class CommonWindowController {
    private final static Logger logger = Logger.getLogger(CommonWindowController.class);
    @FXML
    private TextField txaAdminField;
    @FXML
    private HBox adminBox;
    @FXML
    private Button btnAtack;
    @FXML
    private Label lblMyRank;
    @FXML
    private TableView tblActiveGamers;
    @FXML
    private TableView tblPassiveGamers;
    @FXML
    private TableColumn<Gamer, String> colActiveNicks;
    @FXML
    private TableColumn<Gamer, Integer> colActiveRank;
    @FXML
    private TableColumn<Gamer, String> colPassiveNicks;
    @FXML
    private Label lblLogin;
    @FXML
    private TextArea txtMassage;
    @FXML
    private TextArea txaChat;
    private Stage gameWindow;
    private static Stage waitAnswerWindow;
    private ServerListener listener;
    private String enemy;
    private final static String WAITANSWERFORM = "/views/WaitAnswer.fxml";
    protected final static String ANSWERFORM = "/views/Answer.fxml";
    private RegController regController;
    private GameController gameController;
    private List<Gamer> onlineGamers = new ArrayList<>();
    private List<Gamer> inGamePlayers = new ArrayList<>();
    private static CommonWindowController cwController;

    public CommonWindowController() {
        cwController = this;
    }

    public static CommonWindowController getCwController() {
        return cwController;
    }

    /**
     * controller initialization method
     */
    @FXML
    private void initialize() {
        listener = ServerListener.getListener();
        regController = RegController.getRegController();
        tblActiveGamers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        colActiveNicks.setCellValueFactory(new PropertyValueFactory<Gamer, String>("name"));
        colActiveRank.setCellValueFactory(new PropertyValueFactory<Gamer, Integer>("rank"));
        colPassiveNicks.setCellValueFactory(new PropertyValueFactory<Gamer, String>("name"));
        tblActiveGamers.setItems(FXCollections.observableArrayList(onlineGamers));
        tblPassiveGamers.setItems(FXCollections.observableArrayList(inGamePlayers));
        lblLogin.setText(regController.getUsername());
        listener.setCommonWindowController(this);
    }

    /**
     * method for processing keystrokes btnAtack
     * @param event press the button
     */
    @FXML
    public void pressBtnAtack(ActionEvent event) {
        String key;
        Gamer selectedGamer = (Gamer) tblActiveGamers.getSelectionModel().getSelectedItem();
        if (selectedGamer == null) return;
        key = "INVITE";
        enemy = selectedGamer.getName();
        listener.setEnemy(enemy);
        try {
            listener.getOutClientXML().send(key, listener.getUsername(), enemy);
        } catch (XMLStreamException e) {
            logger.error("INVITE error", e);
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                showWaitAnswerWindow(event, WAITANSWERFORM);
            }
        });
    }

    /**
     * method for processing keystrokes btnBanPlayer
     * @param event press the button
     */
    @FXML
    public void btnBanPlayerPressed(ActionEvent event) {
        String playerNickName = txaAdminField.getText();
        if (!playerNickName.isEmpty()) {
            sendAdminResponse("BAN PLAYER", playerNickName);
        } else {
            DialogManager.showInfoDialog(regController.getComWindow(), "INFO", "Please print the user nickname");
        }
    }

    /**
     * method for processing keystrokes btnBanIp
     * @param event press the button
     */
    @FXML
    public void btnBanIpPressed(ActionEvent event) {
        String playerIP = txaAdminField.getText();
        if (!playerIP.isEmpty()) {
            sendAdminResponse("BAN IP", playerIP);
        } else {
            DialogManager.showInfoDialog(regController.getComWindow(), "INFO", "Please print the user IP");
        }
    }

    /**
     * method for processing keystrokes btnUnBanPlayer
     * @param event press the button
     */
    @FXML
    public void btnUnBanPlayerPressed(ActionEvent event) {
        String playerNickName = txaAdminField.getText();
        if (!playerNickName.isEmpty()) {
            sendAdminResponse("UNBAN PLAYER", playerNickName);
        } else {
            DialogManager.showInfoDialog(regController.getComWindow(), "INFO", "Please print the user nickname");
        }
    }

    /**
     * method for processing keystrokes btnUnBanIp
     * @param event press the button
     */
    @FXML
    public void btnUnBanIpPressed(ActionEvent event) {
        String playerIP = txaAdminField.getText();
        if (!playerIP.isEmpty()) {
            sendAdminResponse("UNBAN IP", playerIP);
        } else {
            DialogManager.showInfoDialog(regController.getComWindow(), "INFO", "Please print the user IP");
        }
    }

    /**
     * method for processing keystrokes btnReboot
     * @param event press the button
     */
    @FXML
    public void btnRebootPressed(ActionEvent event) {
        sendAdminResponse("REBOOT", "REBOOT");
    }

    /**
     * method for processing keystrokes btnShutdown
     * @param event press the button
     */
    @FXML
    public void btnShutdownPressed(ActionEvent event) {
        sendAdminResponse("SHUTDOWN", "SHUTDOWN");
    }

    /**
     * method for sending responses from admin to server
     */
    private void sendAdminResponse(String key, String value) {
        try {
            listener.getOutClientXML().send(key, value);
        } catch (XMLStreamException e) {
            logger.error("Error in sendAdminResponse(String key, String value)", e);
        }
    }

    /**
     * method for creating response windows
     * @param event press the button
     * @param form form Answer or form WaitAnswer
     */
    protected void showWaitAnswerWindow(ActionEvent event, String form) {
        Stage stage = new Stage();
        waitAnswerWindow = stage;
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(form));
            logger.info("Load " + form + " is successfully");
        } catch (IOException e) {
            logger.error("Can not load " + form, e);
        }
        stage.setTitle("Sea Battle 2018");
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.setX(regController.getComWindow().getX() + 200);
        stage.setY(regController.getComWindow().getY() + 100);
        stage.initStyle(StageStyle.UNDECORATED);
        PauseTransition delay = new PauseTransition(Duration.seconds(11));
        delay.setOnFinished(event1 -> stage.close());
        delay.play();
        stage.show();
    }

    public void hideWaitAnswerWindow() {
        waitAnswerWindow.hide();
    }

    /**
     * method for sending messages from chat (from txtMassage)
     */
    private void sendButtonAction() {
        String msg = txtMassage.getText();
        if (!txtMassage.getText().isEmpty()) {
            try {
                listener.getOutClientXML().send("MSG", msg);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtMassage.setText("");
            }
        });
    }

    /**
     * method of processing keystrokes in the txtMassage
     * @param event press the button
     */
    public void sendMethod(KeyEvent event)  {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    /**method for creating and updating tblActiveGamers
     *
     * @param onlineGamersFromListener (list of online gamers from server)
     */
    public void createActiveList(List onlineGamersFromListener) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tblActiveGamers.getItems().clear();
                tblActiveGamers.getItems().addAll(onlineGamersFromListener);
            }
        });
    }

    /**method for creating and updating tblActiveGamers
     *
     * @param onGameGamersFromListener (list of ingame gamers from server)
     */
    public void createPassiveList(List onGameGamersFromListener) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tblPassiveGamers.getItems().clear();
                tblPassiveGamers.getItems().addAll(onGameGamersFromListener);
            }
        });
    }

    /**
     * method for creating a game window (GameWindow form)
     */
    public void showGameWindow() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = new Stage();
                Parent root = null;
                gameWindow = stage;
                try {
                    root = FXMLLoader.load(getClass().getResource(MainLauncher.getPropertyForms().getProperty("GameWindow")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setOnCloseRequest((WindowEvent e) -> {
                    try {
                        if (!gameController.isGameFinish()) {
                            ServerListener.getListener().getOutClientXML().send("SURRENDER", lblLogin.getText());
                        }
                    } catch (XMLStreamException e1) {
                        logger.error("SURRENDER error", e1);
                    }
                    btnAtack.setDisable(false);
                });
                stage.setTitle("Sea battle");
                stage.setScene(new Scene(root, 700, 500));
                stage.setResizable(false);
                btnAtack.setDisable(true);
                stage.show();
            }
        });
    }

    public String getEnemy() {
        return enemy;
    }

    public TextArea getTxaChat() {
        return txaChat;
    }

    public Button getBtnAtack() {
        return btnAtack;
    }

    public Label getLblMyRank() {
        return lblMyRank;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void hideGameWindow() {
        gameWindow.hide();
    }

    public Stage getGameWindow() {
        return gameWindow;
    }

    public HBox getAdminBox() {
        return adminBox;
    }
}
