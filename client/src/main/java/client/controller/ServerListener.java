package client.controller;

import client.MainLauncher;
import client.controller.models.Gamer;
import client.controller.utils.DialogManager;
import client.xmlservice.InClientXML;
import client.xmlservice.OutClientXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implemets Runnable to work with the server and create a socket,
 * streams (InClientXML, OutClientXML), and thread of Server listener
 * class is implemented as Singleton
 *
 * @author Dmytro Cherevko
 * @version 1.0
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
     *
     * @param hostname host of server
     * @param port     port of server
     */
    public void connect(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            inClientXML = new InClientXML(socket);
            outClientXML = new OutClientXML(socket);
            regController.getRegButton().setDisable(false);
            regController.getSignButton().setDisable(false);
            regController.getBtnConnect().setText("Disconnect");
            isConnect = true;
            serverListenerThread = new Thread(this);
            serverListenerThread.start();
            logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
            DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(), "Server info", "You connect to server");
        } catch (IOException e) {
            DialogManager.showErrorDialog(MainLauncher.getPrimaryStageObj(), "Server Error", "Could not connect to server");
            logger.error("Could not Connect to server");
        }
    }

    /**
     * method running in the thread to listen messages from the server
     */
    @Override
    public void run() {
        while (!socket.isClosed() && isConnect()) {
            try {
                inClientXML.setReader(inClientXML.getFactory().createXMLStreamReader(inClientXML.getFileReader()));
                XMLStreamReader reader = inClientXML.getReader();
                while (!socket.isClosed() && reader.hasNext()) {
                    if (reader.getEventType() == 1 && "key".equals(reader.getLocalName())) {
                        reader.next();
                        switch (inClientXML.checkValue(reader)) {
                            case "LOG IN": {
                                String value = inClientXML.checkValue(reader);
                                if ("success!".equals(value)) {
                                    regController.showCommonWindow(false);
                                    break;
                                }
                                if ("success! admin access.".equals(value)) {
                                    regController.showCommonWindow(true);
                                    break;
                                }
                                DialogManager.showInfoDialog(getCurrentWindow(), "Log in INFO", value);
                                break;
                            }
                            case "REG": {
                                String value = inClientXML.checkValue(reader);
                                DialogManager.showInfoDialog(getCurrentWindow(), "Registration INFO", value);
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
                                if (commonWindowController!= null) {
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
                                }

                                break;
                            }
                            case "INGAME PLAYERS": {
                                int countOfPlayers = Integer.parseInt(inClientXML.checkValue(reader));
                                listOnGame.clear();
                                if (commonWindowController!= null) {
                                    for (int i = 1; i <= countOfPlayers; i++) {
                                        String name = inClientXML.checkValue(reader);
                                        listOnGame.add(new Gamer(name));
                                    }

                                    commonWindowController.createPassiveList(listOnGame);
                                }

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
                                        DialogManager.showInfoDialog(getCurrentWindow(), "SHIP LOCATION", value);
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
                                        gameController.setShootByEnemy(result, x1, y1);
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
                                            gameController.setShootByMe(value);
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
                                    break;
                                }
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        gameController.setShootByMe(value);
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
                                DialogManager.showInfoDialog(getCurrentWindow(), "Server info", value);
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
                logger.error("Error in ServerListenerThread run() XMLStreamException");
                isConnect = false;
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
        serverListenerThread.interrupt();
        isConnect = false;
        try {
            outClientXML.getWriter().close();
            if (inClientXML.getReader() != null) {
                inClientXML.getReader().close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Error while trying to close the socket", e);
                }
            }
        } catch (XMLStreamException e1) {
            logger.error("XMLStreamException. Error in method void disconnect() ", e1);
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (commonWindowController.getGameWindow() != null)
                    commonWindowController.hideGameWindow();
                if (regController.getComWindow() != null)
                    regController.hideCommonWindow();
                MainLauncher.getPrimaryStageObj().show();
            }
        });

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                regController.getSignButton().setDisable(true);
                regController.getRegButton().setDisable(true);
                regController.getBtnConnect().setText("Connect");
            }
        });
        DialogManager.showInfoDialog(MainLauncher.getPrimaryStageObj(), "Server info", "You disconnect from server");
    }

    public Stage getCurrentWindow() {
        if (commonWindowController != null && commonWindowController.getGameWindow() != null)
            return commonWindowController.getGameWindow();
        if (regController != null && regController.getComWindow() != null) {
            return regController.getComWindow();
        }
        return MainLauncher.getPrimaryStageObj();
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