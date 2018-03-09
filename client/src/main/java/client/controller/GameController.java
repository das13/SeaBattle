package client.controller;

import client.controller.models.Cell;
import client.controller.models.Ship;
import client.xmlservice.OutClientXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

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
    private Label enemyLogin;

    final static Logger logger = Logger.getLogger(RegController.class);
    public static final int TILE_SIZE = 25;
    public static final int W = 250;
    private static final int H = 250;
    private static final int X_TILES = W / TILE_SIZE;
    private static final int Y_TILES = H / TILE_SIZE;
    private int position = 0;
    private int length = 1;
    private OutClientXML outClientXML;
    private ServerListener listener;

    private static GameController gameController;
    private boolean startgame = false;
    private int x1, y1, y2, x2;

    public GameController() {
        gameController = this;
    }

    public static GameController getCwController() {
        return gameController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listener = ServerListener.getListener();
        createField(userPane, false);
        createField(enemyPane, true);
        ServerListener.getListener().setGameController(this);
        outClientXML = ServerListener.getListener().getOutClientXML();
    }

    private void createField(Pane pane, boolean isEnemy) {
        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Cell cell = new Cell(x,y, isEnemy);
                pane.getChildren().add(cell);
            }
        }
    }

    public void sendAnswer(int x1, int y1) {
        this.x1 = x1;
        this.y1 = y1;
        x2 = (position == 0 ? x1 + length - 1: x1);
        y2 = (position == 0 ? y1 : y1 + length - 1);
        System.out.println("Ship" + x1 + y1 +x2 +y2);
        String[] location = new String[4];
        location[0] = "" + x1; //x1
        location[1] = "" + y1; //y1
        location[2] = "" + x2; //x2
        location[3] = "" + y2; //y2
        try {
            System.out.println("socket "+ ServerListener.getListener().getSocket().isConnected());
            outClientXML.send("SHIP LOCATION", location);
        } catch (XMLStreamException e) {
            logger.error("SHIP LOCATION error",e);
            System.out.println("socket closed:"+ ServerListener.getListener().getSocket().isClosed());
        }

/*        Platform.runLater(new Runnable() {
            @Override
            public void run() {
        try {
            System.out.println("socket "+ServerListener.getListener().getSocket().isConnected());
            ServerListener.getListener().getOutClientXML().send("SHIP LOCATION", location);
        } catch (XMLStreamException e) {
            logger.error("SHIP LOCATION error",e);
            System.out.println("socket "+ServerListener.getListener().getSocket().isConnected());
        }
            }
        });*/

    }

    public void setShip(){
        Ship ship = new Ship(x1, y1,length ,position);
        ship.setShip();
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



}