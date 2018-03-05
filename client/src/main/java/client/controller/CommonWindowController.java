package client.controller;


import client.controller.models.Gamer;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CommonWindowController{
    final static Logger logger = Logger.getLogger(ServerListener.class);

    @FXML
    private TableView tblActiveGamers;
    @FXML
    private TableView tblPassiveGamers;
    @FXML
    private TableColumn<Gamer, String> colActiveNicks;
    @FXML
    private TableColumn<Gamer, Integer> colActiveWins;
    @FXML
    private TableColumn<Gamer, Integer> colActiveLoses;
    @FXML
    private TableColumn<Gamer, String> colPassiveNicks;
    @FXML
    private Label lblLogin;
    @FXML
    private Label lblWins;
    @FXML
    private Label lblLoses;
    private static Stage waitAnswerWindow;
    private String key;
    private String value;

    private static String enemy;


    private ObservableList<Gamer> gamerActiveList = FXCollections.observableArrayList(createActiveList());

    private ObservableList<Gamer> gamerPassiveList = FXCollections.observableArrayList(createPassiveList());

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
        tblActiveGamers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        colActiveNicks.setCellValueFactory(new PropertyValueFactory<Gamer, String>("name"));
        colActiveWins.setCellValueFactory(new PropertyValueFactory<Gamer, Integer>("wins"));
        colActiveLoses.setCellValueFactory(new PropertyValueFactory<Gamer, Integer>("loses"));
        colPassiveNicks.setCellValueFactory(new PropertyValueFactory<Gamer, String>("name"));
        tblActiveGamers.setItems(gamerActiveList);
        tblPassiveGamers.setItems(gamerPassiveList);
        lblLogin.setText(ServerListener.getUsername());
    }

    @FXML
    public void pressBtnAtack(ActionEvent event) {
        Gamer selectedGamer = (Gamer) tblActiveGamers.getSelectionModel().getSelectedItem();
        if (selectedGamer == null) return;
        key = "INVITE";
        enemy = selectedGamer.getName();
        try {
            ServerListener.getOutClientXML().send(key, ServerListener.getUsername(), enemy);
        } catch (XMLStreamException e) {
            logger.error("INVITE error", e);
        }
        showWaitAnswerWindow(event);
    }

    private void showWaitAnswerWindow(ActionEvent event) {
        Stage stage = new Stage();
        waitAnswerWindow = stage;
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/views/WaitAnswer.fxml"));
            logger.info("Load WaitAnswer.fxml is successfully");
        } catch (IOException e) {
            logger.info("Can not load WaitAnswer.fxml", e);
            logger.error("Can not load WaitAnswer.fxml", e);
        }
        stage.setTitle("Sea Battle 2018");
        Scene scene = new Scene(root,300,200);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        stage.initStyle(StageStyle.UNDECORATED);
        PauseTransition delay = new PauseTransition(Duration.seconds(10));
        delay.setOnFinished( event1 -> stage.close() );
        delay.play();
        stage.show();
    }
    public static void hideWaitAnswerWindow() {
        waitAnswerWindow.hide();
    }
    /*
    @FXML
    public void pressBtnAtack(ActionEvent event) {
        Gamer selectedGamer = (Gamer) tblActiveGamers.getSelectionModel().getSelectedItem();
        if (selectedGamer == null) return;
        key = "INVITE";
        value = selectedGamer.getName();
        try {
            ServerListener.getOutClientXML().send(key, value);
        } catch (XMLStreamException e) {
            logger.error("INVITE error", e);
        }
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/views/GameWindow.fxml"));
            logger.info("Load GameWindow.fxml is successfully");
        } catch (IOException e) {
            logger.info("Can not load GameWindow.fxml", e);
            logger.error("Can not load GameWindow.fxml", e);
        }
        stage.setTitle("Sea Battle 2018");
        Scene scene = new Scene(root,600,400);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        stage.show();
    }
*/

    public List<Gamer> createActiveList() {
        List<Gamer> gamers = new ArrayList<>();
        gamers.add(new Gamer("Ivan", 1, 2));
        gamers.add(new Gamer("Ivan2", 4, 2));
        gamers.add(new Gamer("Ivan", 1, 2));
        gamers.add(new Gamer("Ivan2", 4, 2));
        gamers.add(new Gamer("Ivan", 1, 2));
        gamers.add(new Gamer("Ivan2", 4, 2));
        gamers.add(new Gamer("Ivan", 1, 2));
        gamers.add(new Gamer("Ivan2", 4, 2));

        return gamers;
    }

    public List<Gamer> createPassiveList() {
        List<Gamer> gamers = new ArrayList();
        gamers.add(new Gamer("Ivan", 1, 2));
        gamers.add(new Gamer("Ivan2", 4, 2));
        gamers.add(new Gamer("Ivan", 1, 2));
        gamers.add(new Gamer("Ivan2", 4, 2));
        gamers.add(new Gamer("Ivan", 1, 2));
        gamers.add(new Gamer("Ivan2", 4, 2));
        gamers.add(new Gamer("Ivan", 1, 2));
        gamers.add(new Gamer("Ivan2", 4, 2));
        return gamers;
    }
    public static String getEnemy() {
        return enemy;
    }

}


