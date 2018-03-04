package seaBattle.controller;

import seaBattle.model.GameThread;
import seaBattle.model.Player;

public class GameController {
    private PlayerController playerController1;
    private PlayerController playerController2;
    private int countOfPlayers;
    private GameThread game;

    public GameController(Player player, PlayerController playerController) {
        game = new GameThread(player);
        playerController1 = playerController;
        countOfPlayers = 1;
    }

    public void startGame(Player player) {
        game.setSecondPlayerThread(player);
      /*  while(true) {
            switch ()
        }*/
    }
}
