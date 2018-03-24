package client.controller;

import client.controller.models.Cell;
import client.controller.models.FieldDesignation;
import client.controller.models.Ship;
import client.controller.utils.DialogManager;
import client.xmlservice.OutClientXML;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *class controller for working with a GameWindow form
 *@author Dmytro Cherevko
 *@version 1.0
 */

public class GameController implements Initializable {

    @FXML
    private Label lblEnemyLogin;
    @FXML
    private Label lblUserLogin;
    @FXML
    private Label countShip1p;
    @FXML
    private Label countShip2p;
    @FXML
    private Label countShip3p;
    @FXML
    private Label countShip4p;
    @FXML
    private Pane userPane;
    @FXML
    private Pane enemyPane;
    @FXML
    private Button btnSurrender;
    @FXML
    private Pane paneColumn1;
    @FXML
    private Pane paneRow1;
    @FXML
    private Pane paneColumn2;
    @FXML
    private Pane paneRow2;
    @FXML
    private Label lblResultGameUser;
    @FXML
    private Label lblResultGameEnemy;
    @FXML
    private ProgressBar prgEnemy;
    @FXML
    private ProgressBar prgUser;
    @FXML
    private HBox enemyHbox;
    @FXML
    private HBox userHbox;
    @FXML
    private TextArea txaGameInfo;
    private final static Logger logger = Logger.getLogger(GameController.class);
    private static final int TILE_SIZE = 25;
    private static final int W = 250;
    private static final int H = 250;
    private static final int X_TILES = W / TILE_SIZE;
    private static final int Y_TILES = H / TILE_SIZE;
    private int position = 0;
    private int length = 1;
    private int count1p = 4;
    private int count2p = 3;
    private int count3p = 2;
    private int count4p = 1;
    private OutClientXML outClientXML;
    private ServerListener listener;
    private boolean isGameStart;
    private static GameController gameController;
    private boolean isFinishSet;
    private boolean isGameFinish;
    private int x1, y1;
    private CommonWindowController commonWindowController;

    public GameController() {
        gameController = this;
    }

    public static GameController getGameController() {
        return gameController;
    }

    /**
     * Called to initialize a controller after its root element has been completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listener = ServerListener.getListener();
        commonWindowController = CommonWindowController.getCwController();
        createField(userPane, false);
        createField(enemyPane, true);
        listener.setGameController(this);
        outClientXML = ServerListener.getListener().getOutClientXML();
        lblEnemyLogin.setText(listener.getEnemy());
        lblUserLogin.setText(listener.getUsername());
        CommonWindowController.getCwController().setGameController(this);
    }

    /**
     * Method for filling the user and enemy fields for the game
     * @param pane Pane for filling
     * @param isEnemy enemy or user field
     */
    private void createField(Pane pane, boolean isEnemy) {
        char row = 65;
        for (int i = 0; i < 10; i++) {
            if (!isEnemy) {
                paneColumn1.getChildren().add(new FieldDesignation(String.valueOf(i + 1), i, 0));
                paneRow1.getChildren().add(new FieldDesignation(String.valueOf(row++), 0, i));
            } else {
                paneColumn2.getChildren().add(new FieldDesignation(String.valueOf(i + 1), i, 0));
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

    /**
     * method for sending messages to the server about the shot
     * @param x1 shot coordinate
     * @param y1 shot coordinate
     */
    private void shoot(int x1, int y1) {
        if (isGameFinish) {
            DialogManager.showInfoDialog(commonWindowController.getGameWindow(), "GAME INFO", "Game OVER");
            return;
        }
        if (isGameStart) {
            this.x1 = x1;
            this.y1 = y1;
            try {
                outClientXML.send("SHOOT", "" + y1, "" + x1);
            } catch (XMLStreamException e) {
                logger.error("Error in void shoot(int x1, int y1", e);
            }
        } else {
            DialogManager.showInfoDialog(commonWindowController.getGameWindow(), "GAME INFO", "Game is not started");
        }
    }

    /**
     * method for sending a request for ship location
     * @param x1 coordinate of head of ship
     * @param y1 coordinate of head of ship
     */
    private void sendAnswer(int x1, int y1) {
        this.x1 = x1;
        this.y1 = y1;
        int x2 = (position == 0 ? x1 + length - 1 : x1);
        int y2 = (position == 0 ? y1 : y1 + length - 1);
        try {
            outClientXML.send("SHIP LOCATION", y1, x1, y2, x2);
        } catch (XMLStreamException e) {
            logger.error("Error in void sendAnswer(int x1, int y1). Failed to send request for ship location", e);
        }
    }

    /**
     * method for setting ships on the field
     */
    public void setShip() {
        if (!isFinishSet) {
            Ship ship = new Ship(x1, y1, length, position);
            ship.setShip();
            updateCounter(length);
        } else {
            DialogManager.showInfoDialog(commonWindowController.getGameWindow(), "GAME INFO", "you have already arranged all the ships");
        }
    }

    /**
     * method for creating a cell on which a shot was fired
     * @param result shot result
     * @param x1 coordinate of shot
     * @param y1 coordinate of shot
     * @param isEnemy on whose field to set
     * @return new Cell
     */
    private Cell createNewCell(String result, int x1, int y1, boolean isEnemy) {
        String who = isEnemy ? "Enemy:" : "You:";
        Cell cell = new Cell(x1, y1);
        if ("HIT".equals(result)) {
            cell.border.setFill(Color.BLACK);
            txaGameInfo.appendText(who + " HIT " + ((char) ('A' + x1)) + " " + (y1 + 1) + "\n");
        }
        if ("MISS".equals(result)) {
            cell.border.setFill(Color.LIGHTBLUE);
            txaGameInfo.appendText(who + " MISS " + ((char) ('A' + x1)) + " " + (y1 + 1) + "\n");
        }
        if ("DESTROY".equals(result) || "VICTORY!".equals(result)) {
            txaGameInfo.appendText(who + " DESTROY " + ((char) ('A' + x1)) + " " + (y1 + 1) + "\n");
            cell.border.setFill(Color.GOLD);
        }
        return cell;
    }

    /**
     * method for displaying the result of a shot on enemy's field
     * @param result result of shot
     */
    public void setShoot(String result) {
        if ("HIT".equals(result) || "MISS".equals(result) || "DESTROY".equals(result)) {
            enemyPane.getChildren().add(createNewCell(result, x1, y1, false));
        }
    }

    /**
     * method for displaying the result of a shot on users's field
     * @param result result of shot
     */
    public void setShootbyEnemy(String result, int x1, int y1) {
        if ("HIT".equals(result) || "MISS".equals(result) || "DESTROY".equals(result)) {
            userPane.getChildren().add(createNewCell(result, x1, y1, true));
        }
    }

    /**
     * Method to update the progress of time on the shot. Color change on the form
     * @param isUser whose turn is now
     */
    public void shootProgress(boolean isUser) {
        ShootProgress shootEnemyProgress;
        ShootProgress shootUserProgress;
        if (isUser) {
            shootUserProgress = new ShootProgress();
            prgUser.progressProperty().bind(shootUserProgress.progressProperty());
            shootUserProgress.updateProgress(0.0, 1.0);
            prgUser.progressProperty().bind(shootUserProgress.progressProperty());
            Thread th1 = new Thread(shootUserProgress);
            th1.start();
            prgEnemy.progressProperty().unbind();
            prgEnemy.setProgress(0.0);
            enemyHbox.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL, null, null)));
            userHbox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
        } else {
            shootEnemyProgress = new ShootProgress();
            prgEnemy.progressProperty().bind(shootEnemyProgress.progressProperty());
            shootEnemyProgress.updateProgress(0.0, 1.0);
            prgEnemy.progressProperty().bind(shootEnemyProgress.progressProperty());
            Thread th2 = new Thread(shootEnemyProgress);
            th2.start();
            prgUser.progressProperty().unbind();
            prgUser.setProgress(0.0);
            enemyHbox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
            userHbox.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL, null, null)));
        }
    }

    /**
     * method for processing the result of the game
     * @param isVictory victory (true) or defeat(false)
     */
    public void resultGame(boolean isVictory) {
        setGameFinish(true);
        setGameStart(false);
        btnSurrender.setDisable(true);
        commonWindowController.getBtnAtack().setDisable(false);
        DialogManager.showInfoDialog(commonWindowController.getGameWindow(), "GAME", "Game over");
        if (isVictory) {
            prgUser.progressProperty().unbind();
            lblResultGameUser.setText("WINNER");
            lblResultGameEnemy.setText("LOSER");
            gameController.getTxaGameInfo().appendText("Server: You WINNER\n");
        } else {
            prgEnemy.progressProperty().unbind();
            lblResultGameUser.setText("LOSER");
            lblResultGameEnemy.setText("WINNER");
            gameController.getTxaGameInfo().appendText("Server: You DEFEAT\n");
        }
    }

    /**
     * method for processing keystrokes btnSurrender
     * @param event prees on button Surrender
     */
    public void pressBtnSurrender(ActionEvent event) {
        txaGameInfo.appendText("YOU: Surrender\n");
        try {
            listener.getOutClientXML().send("SURRENDER", listener.getUsername());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * method for processing the number of remaining ships for installation
     * @param length number of ship decks
     */
    private void updateCounter(int length) {
        if (length == 1) {
            countShip1p.setText(String.valueOf(--count1p));
            return;
        }
        if (length == 2) {
            countShip2p.setText(String.valueOf(--count2p));
            return;
        }
        if (length == 3) {
            countShip3p.setText(String.valueOf(--count3p));
            return;
        }
        countShip4p.setText(String.valueOf(--count4p));
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

    public Pane getUserPane() {
        return userPane;
    }

    public void setGameStart(boolean gameStart) {
        isGameStart = gameStart;
    }

    public Button getBtnSurrender() {
        return btnSurrender;
    }

    public void setFinishSet(boolean finishSet) {
        isFinishSet = finishSet;
    }

    public void setGameFinish(boolean gameFinish) {
        isGameFinish = gameFinish;
    }

    public boolean isGameFinish() {
        return isGameFinish;
    }

    public TextArea getTxaGameInfo() {
        return txaGameInfo;
    }

    /**
     * class extends Task<Integer> to create a task for the animation progress time to shot
     */
    class ShootProgress extends Task<Integer> {
        @Override
        protected Integer call() throws Exception {
            for (int i = 0; i <= 100; i++) {
                updateProgress(i / 100.0 + 0.01, 1.0);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isCancelled()) {
                    return 0;
                }
            }
            return 100;
        }

        @Override
        protected void updateProgress(double workDone, double max) {
            super.updateProgress(workDone, max);
        }
    }
}