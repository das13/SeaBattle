package client.controller;


import client.MainLauncher;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class CommonWindowController implements Initializable{
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
    }

    @FXML
    public void pressBtnAtack(ActionEvent event) {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colActiveNicks.setCellValueFactory(new PropertyValueFactory<Gamer, String>("name"));
        colActiveWins.setCellValueFactory(new PropertyValueFactory<Gamer, Integer>("wins"));
        colActiveLoses.setCellValueFactory(new PropertyValueFactory<Gamer, Integer>("loses"));
        colPassiveNicks.setCellValueFactory(new PropertyValueFactory<Gamer, String>("name"));
        tblActiveGamers.setItems(gamerActiveList);
        tblPassiveGamers.setItems(gamerPassiveList);
    }
}


