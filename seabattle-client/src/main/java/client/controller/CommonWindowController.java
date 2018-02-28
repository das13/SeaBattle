package client.controller;


import client.Main;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class CommonWindowController implements Initializable{

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

    }

    protected void showCommonWindow() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/views/commonWindow.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });
            stage.setTitle("Sea Battle 2018");
            Scene scene = new Scene(root,640,360);
            //scene.getStylesheets().add(0, "resources/css/main.css");
            stage.setScene(scene);
            stage.setMinHeight(360);
            stage.setMinWidth(640);
            // stage.initModality(Modality.WINDOW_MODAL);
            // stage.initOwner(((Node)event.getSource()).getScene().getWindow());
            stage.show();
        });
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

    public void logoutScene() {
        Platform.runLater(() -> {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent window = null;
            try {
                window = (Pane) fmxlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = Main.getPrimaryStageObj();
            Scene scene = new Scene(window);
            stage.setResizable(false);
            stage.setScene(scene);
        });
    }
}


