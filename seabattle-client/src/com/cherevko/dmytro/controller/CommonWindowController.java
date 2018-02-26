package com.cherevko.dmytro.controller;

import com.cherevko.dmytro.Gamer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;


public class CommonWindowController {

    private MainController mainController ;

    public void setMainController(MainController mainController) {
        this.mainController = mainController ;
    }

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

    @FXML
    private void initialize(){
        colActiveNicks.setCellValueFactory(new PropertyValueFactory<Gamer, String>("name"));
        colActiveWins.setCellValueFactory(new PropertyValueFactory<Gamer, Integer>("wins"));
        colActiveLoses.setCellValueFactory(new PropertyValueFactory<Gamer, Integer>("loses"));
        colPassiveNicks.setCellValueFactory(new PropertyValueFactory<Gamer, String>("name"));
        tblActiveGamers.setItems(gamerActiveList);
        tblPassiveGamers.setItems(gamerPassiveList);
    }
    @FXML
    public void pressBtnAtack(ActionEvent event) {

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
}


