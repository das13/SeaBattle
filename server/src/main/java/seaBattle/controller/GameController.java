package seaBattle.controller;

import seaBattle.model.Field;
import seaBattle.model.Player;
import seaBattle.model.Server;
import seaBattle.model.Ship;
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
    private boolean firstPlayerTurn;
    private String str;
    private int countShips1;
    private int countShips2;
    private Timer timer;
    private File thisGame;
    private boolean placedShipEndOne;
    private boolean placedShipEndTwo;
    private boolean endGame;

    class TimerTaskGameChangePlayer extends TimerTask {

        @Override
        public void run() {
            changeCurrentPlayer();
        }
    }

    public void startTimer() {
        timer = new Timer();
        TimerTaskGameChangePlayer timerTaskGameChangePlayer = new TimerTaskGameChangePlayer();
        timer.schedule(timerTaskGameChangePlayer,30000,30000);
    }

    public GameController(PlayerController playerController1, PlayerController playerController2) {
        timer = new Timer();
        this.playerController1 = playerController1;
        this.playerController2 = playerController2;
        currentPlayerController = playerController1;
        field1 = new Field();
        field2 = new Field();
        firstPlayerTurn = true;
        thisGame = new File("game-" + playerController1.getThisPlayer().getLogin() +"VS"+ playerController2.getThisPlayer().getLogin() + ".xml");
    }

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

    public void changePlayer() {
        firstPlayerTurn = !firstPlayerTurn;
    }

    public void endController() {

    }

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
                playerController1.getOutServerXML().send("SHOOT RESULT","DEFEAT!");
                playerController2.getOutServerXML().send("SHOOT RESULT","VICTORY!");
                for (Player player : Server.getAllPlayersSet()){
                    if (player.getLogin().equals(playerController1.getThisPlayer().getLogin())){
                        player.setRank(player.getRank() - 5);
                        player.setStatus("online");
                    }
                    if (player.getLogin().equals(playerController2.getThisPlayer().getLogin())){
                        player.setRank(player.getRank() + 10);
                        player.setStatus("online");
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

    public boolean checkStart(int countShips) {
        if (countShips < 40) {
            return false;
        } else {
//            saveGame();
            return true;
        }
    }

    public boolean checkStartGame() {
        if (countShips1 + countShips2 < 40) {
            return false;
        } else {
            saveGame();
            return true;
        }
    }

    public String setShip(PlayerController playerController, Ship ship) {
        if (playerController.equals(playerController1)){
            str = field1.setShip(ship);
            if(str.equals("OK")) {
                countShips1 += ship.getHealth();
            }
            if (checkStart(countShips1)) {
                str ="PLACED ENDED";
                placedShipEndOne = true;
            }
        }else {
            str = field2.setShip(ship);
            if(str.equals("OK")) {
                countShips2 += ship.getHealth();
            }
            if (checkStart(countShips2)) {
                str ="PLACED ENDED";
                placedShipEndTwo = true;
            }
        }

        return str;
    }

    public void saveGame(){
        SaveLoadServerXML.saveGame(currentPlayerController.getThisPlayer().getLogin(), playerController1.getThisPlayer().getLogin(), field1, playerController2.getThisPlayer().getLogin(),field2);
    }

    public PlayerController getPlayerController1() {
        return playerController1;
    }
    public void setPlayerController1(PlayerController playerController1) {
        this.playerController1 = playerController1;
    }

    public PlayerController getPlayerController2() {
        return playerController2;
    }
    public void setPlayerController2(PlayerController playerController2) {
        this.playerController2 = playerController2;
    }

    public PlayerController getCurrentPlayerController() {
        return currentPlayerController;
    }
    public void setCurrentPlayerController(PlayerController currentPlayerController) {
        this.currentPlayerController = currentPlayerController;
    }

    public Field getField1() {
        return field1;
    }
    public void setField1(Field field1) {
        this.field1 = field1;
    }

    public Field getField2() {
        return field2;
    }
    public void setField2(Field field2) {
        this.field2 = field2;
    }

    public boolean isFirstPlayerTurn() {
        return firstPlayerTurn;
    }
    public void setFirstPlayerTurn(boolean firstPlayerTurn) {
        this.firstPlayerTurn = firstPlayerTurn;
    }

    public String getStr() {
        return str;
    }
    public void setStr(String str) {
        this.str = str;
    }

    public int getCountShips1() {
        return countShips1;
    }
    public void setCountShips1(int countShips1) {
        this.countShips1 = countShips1;
    }

    public int getCountShips2() {
        return countShips2;
    }
    public void setCountShips2(int countShips2) {
        this.countShips2 = countShips2;
    }

    public Timer getTimer() {
        return timer;
    }
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public File getThisGame() {
        return thisGame;
    }
    public void setThisGame(File thisGame) {
        this.thisGame = thisGame;
    }

    public boolean isPlacedShipEndOne() {
        return placedShipEndOne;
    }

    public boolean isPlacedShipEndTwo() {
        return placedShipEndTwo;
    }
}