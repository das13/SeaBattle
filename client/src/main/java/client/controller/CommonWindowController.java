package client.controller;


import client.MainLauncher;
import client.controller.models.Gamer;
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
 *@autor Dmytro Cherevko
 *@version 1.0
 */
public class CommonWindowController {
    final static Logger logger = Logger.getLogger(CommonWindowController.class);
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
    private boolean isEnemySurrender;
    private static Stage waitAnswerWindow;
    private String key;
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
     * @param event
     */
    @FXML
    public void pressBtnAtack(ActionEvent event) {
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
     * method for creating response windows
     * @param event
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
            logger.info("Can not load " + form, e);
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
    public void sendButtonAction() {
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
     * @param event
     * @throws IOException
     */
    public void sendMethod(KeyEvent event) throws IOException {
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

    public void setEnemySurrender(boolean enemySurrender) {
        isEnemySurrender = enemySurrender;
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
}
