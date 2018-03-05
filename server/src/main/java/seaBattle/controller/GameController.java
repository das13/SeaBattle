package seaBattle.controller;

import seaBattle.model.Field;
import seaBattle.model.Ship;

import java.util.Timer;
import java.util.TimerTask;

public class GameController extends TimerTask {
    private PlayerController playerController1;
    private PlayerController playerController2;
    //private PlayerController currentPlayerController;
    private Field field1;
    private Field field2;
    private boolean firstPlayerTurn;
    private String str;
    private int countShips1;
    private int countShips2;
    private Timer timer;

    public String setShip(PlayerController playerController, Ship ship) {
        if (playerController.equals(playerController1)){
            str = field1.setShip(ship);
            countShips1 += ship.getHealth();
        }else {
            str = field2.setShip(ship);
            countShips2 += ship.getHealth();
        }
        return str;
    }

    public GameController(PlayerController playerController1, PlayerController playerController2) {
        this.playerController1 = playerController1;
        this.playerController2 = playerController2;
        //currentPlayerController = playerController1;
        field1 = new Field();
        field2 = new Field();
        firstPlayerTurn = true;
    }

    public void run() {

    }

    public boolean checkStart() {
        if (countShips1+countShips2 < 40) {
            return false;
        } else {
            return true;
        }
    }
    public void changePlayer() {
        firstPlayerTurn = !firstPlayerTurn;
    }

    public void endController() {

    }
}