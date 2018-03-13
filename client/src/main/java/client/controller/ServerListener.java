package client.controller;

import client.MainLauncher;
import client.controller.models.Gamer;
import client.controller.utils.DialogManager;
import client.xmlservice.InClientXML;
import client.xmlservice.OutClientXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import org.apache.log4j.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerListener implements Runnable {

    final static Logger logger = Logger.getLogger(ServerListener.class);
    private static OutClientXML outClientXML;
    private String password;
    private Socket socket;
    private String username;
    private RegController regController;
    private CommonWindowController commonWindowController;
    private GameController gameController;
    private InClientXML inClientXML;
    private String enemy;
    public List<Gamer> listOnline = new ArrayList<>();
    public List<Gamer> listOnGame = new ArrayList<>();
    private int rank;

    private ServerListener() {}

    public static ServerListener getListener() {
        return ListenerHolder.listener;
    }

    private static class ListenerHolder {
        private final static ServerListener listener = new ServerListener();
    }

    public void connect(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            inClientXML = new InClientXML(socket);
            outClientXML = new OutClientXML(socket);
            RegController.getRegController().getRegButton().setDisable(false);
            RegController.getRegController().getSignButton().setDisable(false);
            RegController.getRegController().getBtnConnect().setDisable(true);
            new Thread(this).start();
            logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
            DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(),"Server info", "You connect to server");
        } catch (Exception e) {
            DialogManager.showErrorDialog(MainLauncher.getPrimaryStageObj(),"Server Error", "Could not connect to server");
            logger.error("Could not Connect to server", e);
        }
    }

    @Override
    public void run() {
        regController = RegController.getRegController();
        System.out.println("RUN is runed");
        while (!socket.isClosed()) {
            try {
                System.out.println("Enter to while (socket.isConnected()) cycle");
                inClientXML.setReader(inClientXML.getFactory().createXMLStreamReader(inClientXML.getFileReader()));
            } catch (XMLStreamException e) {
                logger.error("XMLStreamException in ServerListener thread", e);
            }
            XMLStreamReader reader;
            reader = inClientXML.getReader();
            try {
                while (reader.hasNext()) {
                    if (reader.getEventType() == 1 && reader.getLocalName().equals("key")) {
                        reader.next();
                        switch (inClientXML.checkValue(reader)) {
                            case "LOG IN": {
                                System.out.println("\n\n\nSERVER:\"LOG IN\"");
                                String value = inClientXML.checkValue(reader);
                                System.out.println("result = \"" + value + "\"");
                                if (value.equals("success!")) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            regController.showCommonWindow();
                                        }
                                    });
                                    break;
                                } else {
                                    DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(),"Log in INFO", value);

                                }
                                break;
                            }
                            case "LOG OUT": {
                                System.out.println("\n\n\nSERVER:\"LOG OUT\"");
                                String value = inClientXML.checkValue(reader);
                                System.out.println("result = \"" + value + "\"");
                                break;
                            }
                            case "REG": {
                                System.out.println("\n\n\nSERVER:\"REG\"");
                                String value = inClientXML.checkValue(reader);
                                System.out.println("result = \"" + value + "\"");
                                DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(),"Registration INFO", value);
                                break;
                            }
                            case "MSG": {
                                System.out.println("\n\n\nSERVER:\"MSG\"");
                                String value = inClientXML.checkValue(reader);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        commonWindowController.getTxaChat().appendText(value + "\n\r");
                                    }
                                });
                                break;
                            }
                            case "INVITE": {
                                System.out.println("\n\n\nSERVER:\"INVITE\"");
                                enemy = inClientXML.checkValue(reader);
                                System.out.println("player \"" + enemy + "\" want to play with you. Use key \" REPLY\" to reply. " +
                                        "value1 must be " + enemy + ", and value2 is your answer (now it is \"AUTOAGGREE\" MODE)");

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        commonWindowController.showWaitAnswerWindow(new ActionEvent(), CommonWindowController.ANSWERFORM);
                                    }
                                });
                                break;
                            }
                            case "REPLY": {
                                System.out.println("\n\n\nSERVER:\"REPLY\"");
                                //сообщение от сервера о том что ответ игрока №n на ваше предложение игры - ...
                                String player1 = inClientXML.checkValue(reader);
                                System.out.println("player \"" + player1 + "\" rejected your invite");
                                break;
                            }
                            case "TURN": {
                                System.out.println("\n\n\nSERVER:\"TURN\"");
                                //сообщение от сервера о том что ответ игрока №n на ваше предложение игры - ...
                                    String turn = inClientXML.checkValue(reader);
                                    if (turn.equals("YES")) {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                gameController.shootProgress(true);
                                            }
                                        });
                                    } else {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                gameController.shootProgress(false);
                                            }
                                        });
                                    }
                                break;
                            }
                            case "START GAME": {
                                System.out.println("\n\n\nSERVER:\"START GAME\"");
                                String value = inClientXML.checkValue(reader);

                                if (value.equals("READY")) {
                                    gameController.setGameStart(true);
                                    DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"START GAME", "READY");
                                    break;
                                }
                                System.out.println("GAME STARTED WITH = \"" + value + "\"");

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        commonWindowController.hideWaitAnswerWindow();
                                        commonWindowController.showGameWindow();
                                    }
                                });
                                break;
                            }
                            case "ONLINE PLAYERS": {
                                System.out.println("\n\n\nSERVER:\"ONLINE PLAYERS\"");
                                int countOfPlayers = Integer.parseInt(inClientXML.checkValue(reader));
                                listOnline.clear();
                                for (int i = 1; i <= countOfPlayers; i++) {
                                    String name = inClientXML.checkValue(reader);
                                    rank = Integer.parseInt(inClientXML.checkValue(reader));
                                    System.out.println("online player#" + i + " - " + name);
                                    if (!name.equals(username)) {
                                        listOnline.add(new Gamer(name, rank));
                                    } else {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                commonWindowController.getLblMyRank().setText(String.valueOf(rank));
                                            }
                                        });
                                    }
                                }
                                commonWindowController.createActiveList(listOnline);
                                break;
                            }
                            case "INGAME PLAYERS": {
                                System.out.println("\n\n\nSERVER:\"INGAME PLAYERS\"");
                                int countOfPlayers = Integer.parseInt(inClientXML.checkValue(reader));
                                listOnGame.clear();
                                for (int i = 1; i <= countOfPlayers; i++) {
                                    String name = inClientXML.checkValue(reader);
                                    System.out.println("online player#" + i + " - " + name);
                                    if (!name.equals(username)) {
                                        listOnGame.add(new Gamer(name, 0));
                                    }
                                }
                                commonWindowController.createPassiveList(listOnGame);
                                break;
                            }
                            case "PLAYER INFO": {
                                System.out.println("\n\n\nSERVER:\"PLAYER INFO\"");
                                String value = inClientXML.checkValue(reader);
                                System.out.println("You are = \"" + value + "\"");
                                break;
                            }
                            case "SHIP": {
                                System.out.println("\n\n\nSERVER:\"SHIP\"");
                                //сообщение от сервера относительно того как игрок пытается поставить корабль - ...
                                //(успех / ошибка / все корабли расставлены и идёт ожидание другого игрока или запуск игры)
                                break;
                            }
                            case "SHIP LOCATION": {
                                System.out.println("\n\n\nSERVER:\"LOCATION\"");
                                String value = inClientXML.checkValue(reader);
                                if (value.equals("OK")) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.setShip();
                                        }
                                    });
                                    break;
                                }
                                if (value.equals("PLACED ENDED")) {

                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.setShip();
                                            gameController.setFinishSet(true);
                                            DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"SERVER INFO", "PLACED ENDED wait for massage about game start");
                                        }
                                    });
                                    break;
                                }
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"SHIP LOCATION", value);
                                    }
                                });
                                break;
                            }
                            case "SHOOT MY SIDE": {
                                System.out.println("\n\n\nSERVER:\"MY SIDE\"");
                                String result = inClientXML.checkValue(reader);
                                int x1 = Integer.parseInt(inClientXML.checkValue(reader));
                                int y1 = Integer.parseInt(inClientXML.checkValue(reader));

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        gameController.setShootbyEnemy(result, x1, y1);
                                    }
                                });
                                break;
                            }
                            case "SHOOT RESULT": {
                                System.out.println("\n\n\nSERVER:\"SHOOT RESULT\"");
                                String value = inClientXML.checkValue(reader);
                                if (value.equals("VICTORY!")) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.setGameFinish(true);
                                            gameController.setGameStart(false);
                                            gameController.setShoot(value);
                                            commonWindowController.getBtnAtack().setDisable(false);
                                            gameController.getLblResultGameUser().setText("WINNER");
                                            gameController.getLblResultGameEnemy().setText("LOSER");
                                            DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"SHOOT RESULT", "Game over");
                                            //   regController.showCommonWindow();
                                        }
                                    });
                                    break;
                                }
                                if (value.equals("DEFEAT!")) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.setGameFinish(true);
                                            gameController.setGameStart(false);
                                            commonWindowController.getBtnAtack().setDisable(false);
                                            gameController.getLblResultGameUser().setText("LOSER");
                                            gameController.getLblResultGameEnemy().setText("WINNER");
                                            //     regController.showCommonWindow();
                                        }
                                    });
                                    DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"SHOOT RESULT", "Game over");
                                    break;
                                }
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        gameController.setShoot(value);
                                    }
                                });
                                //DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"SHOOT RESULT", value);
                                break;
                            }
                            case "SURRENDER": {
                                System.out.println("\n\n\nSERVER:\"SURRENDER\"");
                                String value = inClientXML.checkValue(reader);
                                commonWindowController.setEnemySurrender(true);
                                gameController.getBtnSurrender().setDisable(true);
                                commonWindowController.getBtnAtack().setDisable(false);
                                DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"SURRENDER", value);
                                break;
                            }
                            case "GAME OVER": {
                                System.out.println("\n\n\nSERVER:\"GAME OVER\"");
                                String value = inClientXML.checkValue(reader);
                                DialogManager.showInfoDialog(commonWindowController.getGameWindow(),"GAME OVER", value);
                                break;
                            }
                            case "INFO": {
                                System.out.println("\n\n\nSERVER:\"INFO\"");
                                String value = inClientXML.checkValue(reader);
                                System.out.println("***\"" + value + "\"***");
                                //DialogManager.showInfoDialog(regController.getComWindow(),"SERVER INFO", value);
                                break;
                            }
                        }
                    }
                    if (reader.isEndElement() && "root".equals(reader.getName().toString())) {
                        break;
                    } else {
                        reader.next();
                    }
                }
            } catch (XMLStreamException e) {
                logger.error("Error in ServerListenerThread run() XMLStreamException e");
            }
        }
        System.out.println("RUN IS ENDED");
        disconnect();
    }

    public void disconnect(){
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        commonWindowController.hideGameWindow();
                    } catch (Exception e) {}
                    try {
                        regController.hideCommonWindow();
                    } catch (Exception e) {}
                    MainLauncher.getPrimaryStageObj().show();
                }
            });
            Thread.sleep(1000);
            outClientXML.getWriter().close();
            outClientXML.getWriter2().close();
            RegController.getRegController().getSignButton().setDisable(true);
            RegController.getRegController().getRegButton().setDisable(true);
            regController.getBtnConnect().setDisable(false);
        } catch (XMLStreamException e1) {
            logger.error("Logout error", e1);
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getEnemy() {
        return enemy;
    }

    public OutClientXML getOutClientXML() {
        return outClientXML;
    }

    public  String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCommonWindowController(CommonWindowController commonWindowController) {
        this.commonWindowController = commonWindowController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void setRegController(RegController regController) {
        this.regController = regController;
    }

    public void setEnemy(String enemy) {
        this.enemy = enemy;
    }
}