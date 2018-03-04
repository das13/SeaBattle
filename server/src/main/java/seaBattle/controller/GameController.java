package seaBattle.controller;

import seaBattle.model.Field;
import seaBattle.model.Game;

public class GameController extends Thread {
    private PlayerController playerController1;
    private PlayerController playerController2;
    private PlayerController currentPlayerController;
    private Game game;

    public GameController(PlayerController playerController1, PlayerController playerController2) {
        this.playerController1 = playerController1;
        this.playerController2 = playerController2;
        currentPlayerController = playerController1;
        game = new Game();
    }

    public void run() {
        while (true) {
            // тут должен бвть типа прием расстановки
            break;
        }
        while (true) {
            //хода
        }
    }

    public void changePlayer() {
        if (currentPlayerController.equals(playerController1))
            currentPlayerController = playerController2;
        else
            currentPlayerController = playerController1;
    }

    public void endController() {

    }
}