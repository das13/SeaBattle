package seaBattle.controller;

import seaBattle.model.GameThread;
import seaBattle.model.Player;

public class GameController {

    private int countOfPlayers;
    private GameThread game;

    public GameController(Player player) {
        game = new GameThread(player);
        countOfPlayers = 1;
    }

    public void startGame(Player player) {
        game.setSecondPlayerThread(player);
        while(true) {
           switch ()
        }
    }
}

