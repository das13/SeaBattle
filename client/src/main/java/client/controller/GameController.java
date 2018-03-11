package client.controller;

import client.controller.models.Cell;
import client.controller.models.FieldDesignation;
import client.controller.models.Ship;
import client.controller.utils.DialogManager;
import client.xmlservice.OutClientXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    public Label lblEnemyLogin;
    @FXML
    public Label lblUserLogin;
    @FXML
    public Label countShip1p;
    @FXML
    public Label countShip2p;
    @FXML
    public Label countShip3p;
    @FXML
    public Label countShip4p;
    @FXML
    public RadioButton rb_horizontally;
    @FXML
    public RadioButton rb_verically;
    @FXML
    public RadioButton rb_ship1p;
    @FXML
    public RadioButton rb_ship2p;
    @FXML
    public RadioButton rb_ship3p;
    @FXML
    public RadioButton rb_ship4p;
    @FXML
    public Pane userPane;
    @FXML
    public Pane enemyPane;
    @FXML
    public Button btnSurrender;
    @FXML
    public Pane paneColumn1;
    @FXML
    public Pane paneRow1;
    @FXML
    public Pane paneColumn2;
    @FXML
    public Pane paneRow2;
    public Label lblResultGameUser;
    public Label lblResultGameEnemy;

    @FXML
    private Label enemyLogin;

    final static Logger logger = Logger.getLogger(GameController.class);
    public static final int TILE_SIZE = 25;
    public static final int W = 250;
    private static final int H = 250;
    private static final int X_TILES = W / TILE_SIZE;
    private static final int Y_TILES = H / TILE_SIZE;
    private int position = 0;
    private int length = 1;
    private OutClientXML outClientXML;
    private ServerListener listener;
    private boolean isGameStart;
    private static GameController gameController;
    private boolean isFinishSet;
    private boolean isGameFinish;
    private int x1, y1, y2, x2;

    public GameController() {
        gameController = this;
    }

    public static GameController getGameController() {
        return gameController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listener = ServerListener.getListener();
        createField(userPane, false);
        createField(enemyPane, true);
        listener.setGameController(this);
        outClientXML = ServerListener.getListener().getOutClientXML();
        lblEnemyLogin.setText(listener.getEnemy());
        lblUserLogin.setText(listener.getUsername());
    }


    private void createField(Pane pane, boolean isEnemy) {
        char row = 65; // 'A'
        for (int i = 0; i < 10; i++) {
            if (!isEnemy) {
                paneColumn1.getChildren().add(new FieldDesignation(String.valueOf(i+1), i, 0));
                paneRow1.getChildren().add(new FieldDesignation(String.valueOf(row++), 0, i));
            } else {
                paneColumn2.getChildren().add(new FieldDesignation(String.valueOf(i+1), i, 0));
                paneRow2.getChildren().add(new FieldDesignation(String.valueOf(row++), 0, i));
            }

        }
        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Cell cell = new Cell(x, y);
                pane.getChildren().add(cell);
                if (!isEnemy) {
                    cell.setOnMouseClicked(e -> sendAnswer(cell.getX(), cell.getY()));
                } else {
                    cell.setOnMouseClicked(e -> shoot(cell.getX(), cell.getY()));
                }
            }
        }
    }

    public void shoot(int x1, int y1) {
        if (isGameFinish) {
            DialogManager.showInfoDialog("GAME INFO", "Game OVER");
            return;
        }
        if (isGameStart) {
            this.x1 = x1;
            this.y1 = y1;
            try {
                outClientXML.send("SHOOT", "" + y1, "" + x1);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
        else {
            DialogManager.showInfoDialog("GAME INFO", "Game is not started");
        }
    }

    public void sendAnswer(int x1, int y1) {
        System.out.println(lblEnemyLogin.getText() + lblUserLogin.getText());
        this.x1 = x1;
        this.y1 = y1;
        x2 = (position == 0 ? x1 + length - 1: x1);
        y2 = (position == 0 ? y1 : y1 + length - 1);
        System.out.println("Ship" + x1 + y1 +x2 +y2);
        try {
            System.out.println("socket "+ ServerListener.getListener().getSocket().isConnected());
            outClientXML.send("SHIP LOCATION", y1, x1, y2, x2);
        } catch (XMLStreamException e) {
            logger.error("SHIP LOCATION error",e);
            System.out.println("socket closed:"+ ServerListener.getListener().getSocket().isClosed());
        }
    }

    public void setShip(){
        if (!isFinishSet) {
            Ship ship = new Ship(x1, y1, length, position);
            ship.setShip();
        }
        else {
            DialogManager.showInfoDialog("GAME INFO", "you have already arranged all the ships");
        }

    }

    private Cell createNewCell(String result, int x1, int y1){
            Cell cell = new Cell(x1, y1);
            if (result.equals("HIT")){
                cell.border.setFill(Color.BLACK);
            }
            if (result.equals("MISS")){
                cell.border.setFill(Color.LIGHTBLUE);
            }

            if (result.equals("DESTROY")){
                cell.border.setFill(Color.GOLD);
            }
            return cell;
    }

    public void setShoot(String result) {
        if (result.equals("HIT") || result.equals("MISS") || result.equals("DESTROY")) {
            enemyPane.getChildren().add(createNewCell(result, x1, y1));
        }
    }

    public void setShootbyEnemy(String result, int x1, int y1) {
        if (result.equals("HIT") || result.equals("MISS") || result.equals("DESTROY")) {
            userPane.getChildren().add(createNewCell(result, x1, y1));
        }
    }

    public void selectShip1p(ActionEvent event) {
        length = 1;
    }

    public void selectShip2p(ActionEvent event) {
        length = 2;
    }

    public void selectShip3p(ActionEvent event) {
        length = 3;
    }

    public void selectShip4p(ActionEvent event) {
        length = 4;
    }

    public void selectHorizontally(ActionEvent event) {
        position = 0;
    }

    public void selectVerically(ActionEvent event) {
        position = 1;
    }

    public int getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    public  Pane getUserPane() {
        return userPane;
    }

    public void setGameStart(boolean gameStart) {
        isGameStart = gameStart;
    }

    public void pressBtnSurrender(ActionEvent event) {
        try {
            ServerListener.getListener().getOutClientXML().send("SURRENDER", listener.getUsername());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public Button getBtnSurrender() {
        return btnSurrender;
    }

    public void setFinishSet(boolean finishSet) {
        isFinishSet = finishSet;
    }

    public Label getLblResultGameUser() {
        return lblResultGameUser;
    }

    public Label getLblResultGameEnemy() {
        return lblResultGameEnemy;
    }

    public void setGameFinish(boolean gameFinish) {
        isGameFinish = gameFinish;
    }
}