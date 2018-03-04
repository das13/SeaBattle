package seaBattle.controller;

import seaBattle.model.Field;
import seaBattle.model.Game;

public class GameController extends Thread {
    private PlayerController playerController1;
    private PlayerController playerController2;
    private Game game;
    Field field1;
    Field field2;

    public GameController(PlayerController playerController1, PlayerController playerController2) {
        this.playerController1 = playerController1;
        this.playerController2 = playerController2;
        game = new Game(playerController1.getThisPlayer(),playerController2.getThisPlayer());
    }

    public void run() {
//        System.out.println();
//        playerController1.getOutServerXML().send("PLAYER INFO", "player 1");
//        System.out.println();
//        playerController2.getOutServerXML().send("PLAYER INFO", "player 2");
//        System.out.println();
        while (true) {
            System.out.println("\nЭТАП РАССТАНОВКИ КОРАБЛЕЙ.");
            //расстановка
            break;
        }
        while (true) {
            //хода
        }
    }

    public void endController() {

    }
}