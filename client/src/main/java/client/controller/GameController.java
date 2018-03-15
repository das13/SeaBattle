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
    @FXML
    public Label lblResultGameUser;
    @FXML
    public Label lblResultGameEnemy;
    @FXML
    public ProgressBar prgEnemy;
    @FXML
    public ProgressBar prgUser;
    @FXML
    public HBox enemyHbox;
    @FXML
    public HBox userHbox;
    @FXML
    private TextArea txaGameInfo;
    final static Logger logger = Logger.getLogger(GameController.class);
    public static final int TILE_SIZE = 25;
    public static final int W = 250;
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
    private int x1, y1, y2, x2;
    private ShootProgress shootEnemyProgress;
    private ShootProgress shootUserProgress ;
    private CommonWindowController commonWindowController;

    public GameController() {
        gameController = this;
    }

    public static GameController getGameController() {
        return gameController;
    }

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
            DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"GAME INFO", "Game OVER");
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
            DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"GAME INFO", "Game is not started");
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
            System.out.println("socket "+ listener.getSocket().isConnected());
            outClientXML.send("SHIP LOCATION", y1, x1, y2, x2);
        } catch (XMLStreamException e) {
            logger.error("SHIP LOCATION error",e);
            System.out.println("socket closed:"+ listener.getSocket().isClosed());
        }
    }

    public void setShip(){
        if (!isFinishSet) {
            Ship ship = new Ship(x1, y1, length, position);
            ship.setShip();
            updateCounter(length);
        }
        else {
            DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"GAME INFO", "you have already arranged all the ships");
        }
    }

    private Cell createNewCell(String result, int x1, int y1, boolean isEnemy){
        String who = isEnemy ? "Enemy:" : "You:";
        Cell cell = new Cell(x1, y1);
        if (result.equals("HIT")) {
            cell.border.setFill(Color.BLACK);
            txaGameInfo.appendText(who + " HIT " + ((char)('A' + x1)) + " " + (y1 + 1) + "\n");
        }
        if (result.equals("MISS")) {
            cell.border.setFill(Color.LIGHTBLUE);
            txaGameInfo.appendText(who + " MISS " + ((char)('A' + x1)) + " " + (y1 + 1) + "\n");
        }
        if (result.equals("DESTROY") || result.equals("VICTORY!")) {
            txaGameInfo.appendText(who + " DESTROY " + ((char)('A' + x1)) + " " + (y1 + 1) + "\n");
            cell.border.setFill(Color.GOLD);
        }
        return cell;
    }

    public void setShoot(String result) {
        if (result.equals("HIT") || result.equals("MISS") || result.equals("DESTROY")) {
            enemyPane.getChildren().add(createNewCell(result, x1, y1, false));
        }
    }

    public void setShootbyEnemy(String result, int x1, int y1) {
        if (result.equals("HIT") || result.equals("MISS") || result.equals("DESTROY")) {
            userPane.getChildren().add(createNewCell(result, x1, y1, true));
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

    public  Pane getUserPane() {
        return userPane;
    }

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

    public void setGameStart(boolean gameStart) {
        isGameStart = gameStart;
    }

    public void pressBtnSurrender(ActionEvent event) {
        txaGameInfo.appendText("YOU: Surrender\n");
        try {
            listener.getOutClientXML().send("SURRENDER", listener.getUsername());
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

    public boolean isGameFinish() {
        return isGameFinish;
    }

    public void shootProgress(boolean isUser){
        if (isUser) {
            shootUserProgress = new ShootProgress();
            prgUser.progressProperty().bind(shootUserProgress.progressProperty());
            shootUserProgress.updateProgress(0.0,1.0);
            prgUser.progressProperty().bind(shootUserProgress.progressProperty());
            Thread th1 = new Thread(shootUserProgress);
            th1.start();
            prgEnemy.progressProperty().unbind();
            prgEnemy.setProgress(0.0);
            enemyHbox.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL, null, null)));
            userHbox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
        }else {
            shootEnemyProgress = new ShootProgress();
            prgEnemy.progressProperty().bind(shootEnemyProgress.progressProperty());
            shootEnemyProgress.updateProgress(0.0,1.0);
            prgEnemy.progressProperty().bind(shootEnemyProgress.progressProperty());
            Thread th2 = new Thread(shootEnemyProgress);
            th2.start();
            prgUser.progressProperty().unbind();
            prgUser.setProgress(0.0);
            enemyHbox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
            userHbox.setBackground(new Background(new BackgroundFill(Color.LIGHTCORAL, null, null)));
        }
    }

    public TextArea getTxaGameInfo() {
        return txaGameInfo;
    }

    class ShootProgress extends Task<Integer> {
        int i = 0;
        @Override
        protected Integer call() throws Exception {
            for (i = 0; i <= 100; i++) {
                updateProgress(i/100.0 + 0.01, 1.0);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isCancelled()) {return 0;}
            }
            return 100;
        }

        @Override
        protected void updateProgress(double workDone, double max) {
            super.updateProgress(workDone, max);
        }
    }
}