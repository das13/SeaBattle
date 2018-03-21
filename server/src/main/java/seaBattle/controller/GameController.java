package seaBattle.controller;

import seaBattle.Server;
import seaBattle.model.*;
import seaBattle.xmlservice.SaveLoadServerXML;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Game Controller
 * control logic of game
 * @author Roman Kraskovskiy
 */
public class GameController {
    private PlayerController playerController1;
    private PlayerController playerController2;
    private PlayerController currentPlayerController;
    private PlayerController winner;
    private Field field1;
    private Field field2;
    private String str;
    private int countShips1;
    private int countShips2;
    private Timer timer;
    private File thisGame;
    private boolean endGame = false;

    /**
     * timer task class
     */
    class TimerTaskGameChangePlayer extends TimerTask {

        /**
         * change of turn of player
         */
        @Override
        public void run() {
            changeCurrentPlayer();
        }
    }

    /**
     * start timer every 30 sec
     */
    public void startTimer() {
        timer = new Timer();
        TimerTaskGameChangePlayer timerTaskGameChangePlayer = new TimerTaskGameChangePlayer();
        timer.schedule(timerTaskGameChangePlayer,30000,30000);
    }

    /**
     * constructor
     * @param playerController1 player one
     * @param playerController2 player two
     */
    public GameController(PlayerController playerController1, PlayerController playerController2) {
        timer = new Timer();
        this.playerController1 = playerController1;
        this.playerController2 = playerController2;
        currentPlayerController = playerController1;
        field1 = new Field();
        field2 = new Field();
        thisGame = new File("game-" + playerController1.getThisPlayer().getLogin() +"VS"+ playerController2.getThisPlayer().getLogin() + ".xml");
    }

    /**
     * change player result
     * @param playerController1 turn player
     * @param playerController2 not turn player
     */
    public void changeCurrentPlayerResult(PlayerController playerController1,PlayerController playerController2) {
        currentPlayerController = playerController2;
        playerController2.getOutServerXML().send("TURN","YES");
        playerController1.getOutServerXML().send("TURN","NO");
    }

    /**
     * change turn of players
     */
    public void changeCurrentPlayer(){
        if(currentPlayerController.equals(playerController1)) {
            changeCurrentPlayerResult(playerController1,playerController2);
        } else {
            currentPlayerController = playerController1;
            changeCurrentPlayerResult(playerController2,playerController1);
        }
    }

    /**
     * sets ranks and delete file of game
     * @param playerController loser
     */
    public void gameEndResult(PlayerController playerController,String key) {
        playerController.getOutServerXML().send(key,"DEFEAT!");
        winner.getOutServerXML().send(key,"VICTORY!");
        int count = 0;
        for (Player player : Server.getAllPlayersSet()){
            if (player.getLogin().equals(playerController.getThisPlayer().getLogin())){
                player.setRank(player.getRank() - 5);
                player.setStatus("online");
                count++;
            }
            if (player.getLogin().equals(winner.getThisPlayer().getLogin())){
                player.setRank(player.getRank() + 10);
                player.setStatus("online");
                count++;
            }
            if (count == 2){
                if (new File("game-" + playerController.getThisPlayer().getLogin() +"VS"+ winner.getThisPlayer().getLogin() + ".xml").exists()){
                    File file = new File(new File("game-" + playerController.getThisPlayer().getLogin() +"VS"+ winner.getThisPlayer().getLogin() + ".xml").getPath());
                    file.delete();
                }
            }
        }
        timer.cancel();
        endGame = true;
        playerController1.updateAndSendPlayersInfo();
    }

    /**
     * checking for game end
     */
    public void gameEndChecking() {
        if (countShips1 == 0) {
            gameEndResult(playerController1,"SHOOT RESULT");
        } else if (countShips2 == 0) {
            gameEndResult(playerController2,"SHOOT RESULT");
        }
    }

    /**
     * result of shooting
     * @param playerController fired player
     * @param x shoot row
     * @param y shoot col
     * @return count of player ship
     */
    public int shootResult(PlayerController playerController,Field field,int countShips,int x,int y) {
        str = field.shoot(x,y);
        String y1 = String.valueOf(y);
        String x1 = String.valueOf(x);
        playerController.getOutServerXML().send("SHOOT MY SIDE", str, y1,x1);
        if (!str.equals("MISS")) {
            countShips--;
            saveGame();
        } else {
            changeCurrentPlayer();
        }
        return countShips;
    }
    /**
     * shoot of player
     * @param playerController player
     * @param x row
     * @param y col
     * @return result of shoot
     */
    public String shoot(PlayerController playerController,int x ,int y)  {
        if (!playerController.equals(currentPlayerController)) {
            return "NOT YOUR TURN";
        }
        if (playerController.equals(playerController1)){
            winner = playerController1;
            countShips2 = shootResult(playerController2,field2,countShips2,x,y);
        }else {
            winner = playerController2;
            countShips1 = shootResult(playerController1,field1,countShips1,x,y);
        }
        gameEndChecking();
        if (!endGame) {
            timer.cancel();
            startTimer();
        }
        return str;
    }

    /**
     * surrender of player
     * @param playerController player
     */
    public void surrender(PlayerController playerController) {
        if(playerController.equals(playerController1)) {
            winner = playerController2;
            gameEndResult(playerController1,"SURRENDER RESULT");
        } else {
            winner = playerController1;
            gameEndResult(playerController2,"SURRENDER RESULT");
        }
    }

    /**
     * checking of end of placing of player
     * @param countShips
     * @return true if placing of player was ended
     */
    public boolean checkStart(int countShips) {
        if (countShips < 20) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * checking start of game after placing
     * @return true if two players ended of placing
     */
    public boolean checkStartGame() {
        if (countShips1 + countShips2 < 40) {
            return false;
        } else {
            saveGame();
            return true;
        }
    }

    /**
     * setting ship result
     * @param field field of setting ship player
     * @param countShips count of player ship
     * @param ship player ship
     * @return count of player ship
     */
    public int setShipResult(Field field,int countShips,Ship ship) {
        str = field.setShip(ship);
        if(str.equals("OK")) {
            countShips += ship.getHealth();
        }
        if (checkStart(countShips)) {
            str ="PLACED ENDED";
        }
        return countShips;
    }
    /**
     * ship setting
     * @param playerController player, who set ship
     * @param ship ship for setting
     * @return result of ship setting
     */
    public String setShip(PlayerController playerController, Ship ship) {
        if (playerController.equals(playerController1)){
            countShips1 = setShipResult(field1,countShips1,ship);

        }else {
            countShips2 = setShipResult(field2,countShips2,ship);
        }
        return str;
    }

    /**
     * saving of game
     */
    public void saveGame(){
        SaveLoadServerXML.saveGame(currentPlayerController.getThisPlayer().getLogin(), playerController1.getThisPlayer().getLogin(), field1, playerController2.getThisPlayer().getLogin(),field2);
    }

    /**
     * @return player one
     */
    public PlayerController getPlayerController1() {
        return playerController1;
    }

    /**
     * @return player two
     */
    public PlayerController getPlayerController2() {
        return playerController2;
    }
}