package seaBattle.controller;

import seaBattle.model.*;
import seaBattle.xmlservice.SaveLoadServerXML;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    private PlayerController playerController1;
    private PlayerController playerController2;
    private PlayerController currentPlayerController;
    private Field field1;
    private Field field2;
    private String str;
    private int countShips1;
    private int countShips2;
    private Timer timer;
    private File thisGame;
   /* private boolean placedShipEndOne;
    private boolean placedShipEndTwo;*/
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
     * change turn of players
     */
    public void changeCurrentPlayer(){
        if(currentPlayerController.equals(playerController1)) {
            currentPlayerController = playerController2;
            playerController2.getOutServerXML().send("TURN","YES");
            playerController1.getOutServerXML().send("TURN","NO");
        } else {
            currentPlayerController = playerController1;
            playerController2.getOutServerXML().send("TURN","NO");
            playerController1.getOutServerXML().send("TURN","YES");
        }
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
            str = field2.shoot(x,y);
            String y1 = String.valueOf(y);
            String x1 = String.valueOf(x);
            playerController2.getOutServerXML().send("SHOOT MY SIDE", str, y1,x1);
            /*try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (!str.equals("MISS")) {
                countShips2--;
            } else {
                changeCurrentPlayer();
            }
        }else {
            str = field1.shoot(x,y);
            String y1 = String.valueOf(y);
            String x1 = String.valueOf(x);
            playerController1.getOutServerXML().send("SHOOT MY SIDE", str, y1,x1);
            /*try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            if (!str.equals("MISS")) {
                countShips1--;
            } else {
                changeCurrentPlayer();
            }
        }
       // try {
            if (countShips1 == 0) {
            int count = 0;
                playerController1.getOutServerXML().send("SHOOT RESULT","DEFEAT!");
                playerController2.getOutServerXML().send("SHOOT RESULT","VICTORY!");
                for (Player player : Server.getAllPlayersSet()){
                    if (player.getLogin().equals(playerController1.getThisPlayer().getLogin())){
                        player.setRank(player.getRank() - 5);
                        player.setStatus("online");
                        count++;
                    }
                    if (player.getLogin().equals(playerController2.getThisPlayer().getLogin())){
                        player.setRank(player.getRank() + 10);
                        player.setStatus("online");
                        count++;
                    }
                    if (count == 2){
                        if (new File("game-" + playerController1.getThisPlayer().getLogin() +"VS"+ playerController2.getThisPlayer().getLogin() + ".xml").exists()){
                            File file = new File(new File("game-" + playerController1.getThisPlayer().getLogin() +"VS"+ playerController2.getThisPlayer().getLogin() + ".xml").getPath());
                            file.delete();

                        }
                    }
                }
                timer.cancel();
                endGame = true;
                playerController1.updateAndSendPlayersInfo();
            } else if (countShips2 == 0) {
                playerController1.getOutServerXML().send("SHOOT RESULT","VICTORY!");
                playerController2.getOutServerXML().send("SHOOT RESULT","DEFEAT!");
                for (Player player : Server.getAllPlayersSet()){
                    if (player.getLogin().equals(playerController2.getThisPlayer().getLogin())){
                        player.setRank(player.getRank() - 5);
                        player.setStatus("online");
                    }
                    if (player.getLogin().equals(playerController1.getThisPlayer().getLogin())){
                        player.setRank(player.getRank() + 10);
                        player.setStatus("online");
                    }
                }
                timer.cancel();
                endGame = true;
                playerController1.updateAndSendPlayersInfo();
            }
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
            playerController1.getOutServerXML().send("SURRENDER RESULT","DEFEAT!");
            playerController2.getOutServerXML().send("SURRENDER RESULT","VICTORY!");
        } else {
            playerController2.getOutServerXML().send("SURRENDER RESULT","DEFEAT!");
            playerController1.getOutServerXML().send("SURRENDER RESULT","VICTORY!");
        }
        endGame = true;
        timer.cancel();
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
//            saveGame();
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
     * ship setting
     * @param playerController player, who set ship
     * @param ship ship for setting
     * @return result of ship setting
     */
    public String setShip(PlayerController playerController, Ship ship) {
        if (playerController.equals(playerController1)){
            str = field1.setShip(ship);
            if(str.equals("OK")) {
                countShips1 += ship.getHealth();
            }
            if (checkStart(countShips1)) {
                str ="PLACED ENDED";
              //  placedShipEndOne = true;
            }
        }else {
            str = field2.setShip(ship);
            if(str.equals("OK")) {
                countShips2 += ship.getHealth();
            }
            if (checkStart(countShips2)) {
                str ="PLACED ENDED";
              //  placedShipEndTwo = true;
            }
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