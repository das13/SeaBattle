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

/**
 *Class implemets Runnable to work with the server and create a socket,
 *streams (InClientXML, OutClientXML), and thread of Server listener
 *class is implemented as Singleton
 *
 *@author Dmytro Cherevko
 *@version 1.0
 */

public class ServerListener implements Runnable {

    private final static Logger logger = Logger.getLogger(ServerListener.class);
    private OutClientXML outClientXML;
    private boolean isConnect;
    private Socket socket;
    private String username;
    private RegController regController;
    private CommonWindowController commonWindowController;
    private GameController gameController;
    private InClientXML inClientXML;
    private String enemy;
    private List<Gamer> listOnline = new ArrayList<>();
    private List<Gamer> listOnGame = new ArrayList<>();
    private Thread serverListenerThread;

    private ServerListener() {
    }

    public static ServerListener getListener() {
        return ListenerHolder.listener;
    }

    private static class ListenerHolder {
        private final static ServerListener listener = new ServerListener();
    }

    /**
     * method for creating a connection to the server
     * @param hostname host of server
     * @param port port of server
     */
    public void connect(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            inClientXML = new InClientXML(socket);
            outClientXML = new OutClientXML(socket);
            RegController.getRegController().getRegButton().setDisable(false);
            RegController.getRegController().getSignButton().setDisable(false);
            RegController.getRegController().getBtnConnect().setText("Disconnect");
            isConnect = true;
            serverListenerThread = new Thread(this);
            serverListenerThread.start();
            logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
            DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(), "Server info", "You connect to server");
        } catch (Exception e) {
            DialogManager.showErrorDialog(MainLauncher.getPrimaryStageObj(), "Server Error", "Could not connect to server");
            logger.error("Could not Connect to server", e);
        }
    }

    /**
     * method running in the thread to listen messages from the server
     */
    @Override
    public void run() {
        regController = RegController.getRegController();
        while (!serverListenerThread.isInterrupted() && !socket.isClosed()) {
            try {
                inClientXML.setReader(inClientXML.getFactory().createXMLStreamReader(inClientXML.getFileReader()));
            } catch (XMLStreamException e) {
                logger.error("XMLStreamException in ServerListener thread", e);
            }
            XMLStreamReader reader;
            reader = inClientXML.getReader();
            try {
                while (isConnect && reader.hasNext()) {
                    if (reader.getEventType() == 1 && "key".equals(reader.getLocalName())) {
                        reader.next();
                        switch (inClientXML.checkValue(reader)) {
                            case "LOG IN": {
                                String value = inClientXML.checkValue(reader);
                                if ("success!".equals(value)) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            regController.showCommonWindow(false);
                                        }
                                    });
                                    break;
                                } if ("success! admin access.".equals(value)) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            regController.showCommonWindow(true);
                                        }
                                    });
                                    break;
                                }
                                DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(), "Log in INFO", value);
                                break;
                            }
                            case "LOG OUT": {
                                String value = inClientXML.checkValue(reader);
                                break;
                            }
                            case "REG": {
                                String value = inClientXML.checkValue(reader);
                                DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(), "Registration INFO", value);
                                break;
                            }
                            case "MSG": {
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
                                enemy = inClientXML.checkValue(reader);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        commonWindowController.showWaitAnswerWindow(new ActionEvent(), CommonWindowController.ANSWERFORM);
                                    }
                                });
                                break;
                            }
                            case "REPLY": {
                                String player1 = inClientXML.checkValue(reader);
                                break;
                            }
                            case "TURN": {
                                String turn = inClientXML.checkValue(reader);
                                if ("YES".equals(turn)) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.shootProgress(true);
                                            gameController.getTxaGameInfo().appendText("Server: You turn\n");
                                        }
                                    });
                                } else {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.shootProgress(false);
                                            gameController.getTxaGameInfo().appendText("Server: You wait\n");
                                        }
                                    });
                                }
                                break;
                            }
                            case "START GAME": {
                                String value = inClientXML.checkValue(reader);
                                if ("READY".equals(value)) {
                                    gameController.setGameStart(true);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.getTxaGameInfo().appendText("Server: The game has begun\n");
                                        }
                                    });
                                    break;
                                }
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
                                int countOfPlayers = Integer.parseInt(inClientXML.checkValue(reader));
                                listOnline.clear();
                                for (int i = 1; i <= countOfPlayers; i++) {
                                    String name = inClientXML.checkValue(reader);
                                    int rank = Integer.parseInt(inClientXML.checkValue(reader));
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
                                int countOfPlayers = Integer.parseInt(inClientXML.checkValue(reader));
                                listOnGame.clear();
                                for (int i = 1; i <= countOfPlayers; i++) {
                                    String name = inClientXML.checkValue(reader);
                                    if (!name.equals(username)) {
                                        listOnGame.add(new Gamer(name));
                                    }
                                }
                                commonWindowController.createPassiveList(listOnGame);
                                break;
                            }
                            case "PLAYER INFO": {
                                String value = inClientXML.checkValue(reader);
                                break;
                            }
                            case "SHIP LOCATION": {
                                String value = inClientXML.checkValue(reader);
                                if ("OK".equals(value)) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.setShip();
                                        }
                                    });
                                    break;
                                }
                                if ("PLACED ENDED".equals(value)) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.setShip();
                                            gameController.setFinishSet(true);
                                            gameController.getTxaGameInfo().appendText("Server: PLACED ENDED wait for massage about game start\n");
                                        }
                                    });
                                    break;
                                }
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogManager.showInfoDialog(commonWindowController.getGameWindow(), "SHIP LOCATION", value);
                                    }
                                });
                                break;
                            }
                            case "SHOOT MY SIDE": {
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
                                String value = inClientXML.checkValue(reader);
                                if ("VICTORY!".equals(value)) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.setShoot(value);
                                            gameController.resultGame(true);
                                        }
                                    });
                                    break;
                                }
                                if ("DEFEAT!".equals(value)) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.resultGame(false);
                                        }
                                    });
                                    DialogManager.showInfoDialog(commonWindowController.getGameWindow(), "SHOOT RESULT", "Game over");
                                    break;
                                }
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        gameController.setShoot(value);
                                    }
                                });
                                break;
                            }
                            case "SURRENDER RESULT": {
                                String value = inClientXML.checkValue(reader);
                                if ("VICTORY!".equals(value)) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameController.getBtnSurrender().setDisable(true);
                                            gameController.getTxaGameInfo().appendText("Server: Enemy surrender\n");
                                            gameController.resultGame(true);
                                        }
                                    });
                                    break;
                                }
                                if ("DEFEAT!".equals(value)) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            commonWindowController.getBtnAtack().setDisable(false);
                                            gameController.getTxaGameInfo().appendText("Server: You Surrender\n");
                                            gameController.resultGame(false);
                                        }
                                    });
                                    break;
                                }
                                break;
                            }
                            case "GAME OVER": {
                                String value = inClientXML.checkValue(reader);
                                DialogManager.showInfoDialog(commonWindowController.getGameWindow(), "GAME OVER", value);
                                break;
                            }
                            case "INFO": {
                                String value = inClientXML.checkValue(reader);
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
        if (!serverListenerThread.isInterrupted()) {
            disconnect();
        }
    }

    /**
     * method to disconnect from the server and close streams and windows
     */
    public void disconnect() {

        try {
            outClientXML.getWriter().close();
            isConnect = false;
            serverListenerThread.interrupt();
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        commonWindowController.hideGameWindow();
                    } catch (Exception e) {
                    }
                    try {
                        regController.hideCommonWindow();
                    } catch (Exception e) {
                    }
                    MainLauncher.getPrimaryStageObj().show();
                }
            });

            RegController.getRegController().getSignButton().setDisable(true);
            RegController.getRegController().getRegButton().setDisable(true);
            regController.getBtnConnect().setText("Connect");
        } catch (XMLStreamException e1) {
            logger.error("Logout error", e1);
        }
    }

    public String getEnemy() {
        return enemy;
    }

    public OutClientXML getOutClientXML() {
        return outClientXML;
    }

    public String getUsername() {
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

    public boolean isConnect() {
        return isConnect;
    }
}