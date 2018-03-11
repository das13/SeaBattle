package client.controller;


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


public class CommonWindowController{
    final static Logger logger = Logger.getLogger(ServerListener.class);
    @FXML
    public Button btnAtack;
    public Label lblMyRank;
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
    private boolean isEnemySurrender = false;
    private static Stage waitAnswerWindow;
    private String key;
    private String value;
    private  ServerListener listener;
    private  String enemy;
    private final static String WAITANSWERFORM = "/views/WaitAnswer.fxml";
    protected final static String ANSWERFORM = "/views/Answer.fxml";
    public RegController regController;

    private List<Gamer> onlineGamers = new ArrayList<>();
    private List<Gamer> inGamePlayers = new ArrayList<>();

    private static CommonWindowController cwController;

    public CommonWindowController() {
        cwController = this;
    }

    public static CommonWindowController getCwController() {
        return cwController;
    }

    public Label getLblLogin() {
        return lblLogin;
    }


    @FXML
    private void initialize(){
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

    protected void showWaitAnswerWindow(ActionEvent event, String form) {
        Stage stage = new Stage();
        waitAnswerWindow = stage;
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(form));
            logger.info("Load " + form + "is successfully");
        } catch (IOException e) {
            logger.info("Can not load " + form, e);
            logger.error("Can not load " + form, e);
        }
        stage.setTitle("Sea Battle 2018");
        Scene scene = new Scene(root,300,200);
        stage.setScene(scene);
        stage.setX(RegController.getRegController().comWindow.getX() + 200);
        stage.setY(RegController.getRegController().comWindow.getY() + 100);

        stage.initStyle(StageStyle.UNDECORATED);
        PauseTransition delay = new PauseTransition(Duration.seconds(11));
        delay.setOnFinished( event1 -> stage.close() );
        delay.play();
        stage.show();
    }

    public void hideWaitAnswerWindow(){
        waitAnswerWindow.hide();
    }

    public void sendButtonAction(){
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

    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    public void createActiveList(List onlineGamersFromListener) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tblActiveGamers.getItems().clear();
                tblActiveGamers.getItems().addAll(onlineGamersFromListener);
            }
        });
    }

    public void createPassiveList(List onGameGamersFromListener) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tblPassiveGamers.getItems().clear();
                tblPassiveGamers.getItems().addAll(onGameGamersFromListener);
            }
        });
    }

    public String getEnemy() {
        return enemy;
    }

    public void showGameWindow() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = new Stage();
                Parent root = null;
                gameWindow = stage;
                try {
                    root = FXMLLoader.load(getClass().getResource("/views/GameWindow.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setOnCloseRequest((WindowEvent e) -> {
                    try {
                        if (!isEnemySurrender()) {

                            ServerListener.getListener().getOutClientXML().send("SURRENDER", lblLogin.getText());
                        }
                        setEnemySurrender(false);
                    } catch (XMLStreamException e1) {
                        logger.error("SURRENDER error", e1);
                    }
                    btnAtack.setDisable(false);
                    RegController.getRegController().comWindow.show();
                });
                stage.setTitle("Sea battle");
                stage.setScene(new Scene(root, 700, 400));
                stage.setResizable(false);
                RegController.getRegController().comWindow.hide();
                btnAtack.setDisable(true);
                stage.show();

            }
        });
    }



    public TextArea getTxaChat() {
        return txaChat;
    }

    public void setEnemySurrender(boolean enemySurrender) {
        isEnemySurrender = enemySurrender;
    }

    public boolean isEnemySurrender() {
        return isEnemySurrender;
    }

    public Button getBtnAtack() {
        return btnAtack;
    }

    public Label getLblMyRank() {
        return lblMyRank;
    }
}


