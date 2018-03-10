package client.controller;


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
import java.util.Scanner;


public class ServerListener implements Runnable{

    final static Logger logger = Logger.getLogger(ServerListener.class);

    private static OutClientXML outClientXML;
    private String password;
    private Socket socket;
    private String hostname;
    private int port;
    private String username;
    private RegController regController;
    private CommonWindowController commonWindowController;
    private GameController gameController;
    private InClientXML inClientXML;
    private Scanner scanner;
    private String key;
    private boolean isConnect;
    private String enemy;
    public  List<Gamer> listOnline = new ArrayList<>();
    public  List<Gamer> listOnGame = new ArrayList<>();
    private Thread th;
    private ServerListener listener = getListener();

    private ServerListener(){
    }
    public static ServerListener getListener() {
        return ListenerHolder.listener;
    }
    private static class ListenerHolder{
        private final static ServerListener listener = new ServerListener();
    }

    public void connect(String hostname, int port){
        try {
            socket = new Socket(hostname, port);
            isConnect = true;
            inClientXML = new InClientXML(socket);
            outClientXML = new OutClientXML(socket);
            RegController.getRegController().getRegButton().setDisable(false);
            RegController.getRegController().getSignButton().setDisable(false);
            RegController.getRegController().getBtnConnect().setDisable(true);

            new Thread(this).start();

            logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
            showDialogInfo("Server info", "You connect to server");
        } catch (Exception e) {

            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    DialogManager.showErrorDialog("Server Error", "Could not connect to server");
                }
            });
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
           XMLStreamReader reader = inClientXML.getReader();

           try {
               while (reader.hasNext()) {
                   reader = inClientXML.getReader();
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
                                   showDialogInfo("Log in INFO", value);
                                   break;
                               }
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
                               showDialogInfo("Registration INFO", value);
                               break;
                           }
                           case "MSG": {
                               //делать в последнюю очередь
                               System.out.println("\n\n\nSERVER:\"MSG\"");
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
                           case "START GAME": {
                               System.out.println("\n\n\nSERVER:\"START GAME\"");
                               String value = inClientXML.checkValue(reader);
                               System.out.println("GAME STARTED WITH = \"" + value + "\"");
                               Platform.runLater(new Runnable() {
                                   @Override
                                   public void run() {
                                       commonWindowController.hideWaitAnswerWindow();
                                       commonWindowController.showGameWindow(enemy);
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
                                   System.out.println("online player#" + i + " - " + name);
                                   if (!name.equals(username)) {
                                       listOnline.add(new Gamer(name, 0, 0));
                                   }
                               }

                               CommonWindowController.getCwController().createActiveList(listOnline);

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
                                       listOnGame.add(new Gamer(name, 0, 0));
                                   }
                               }

                               CommonWindowController.getCwController().createPassiveList(listOnGame);
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
                               System.out.println("\n\n\nSERVER:\"SHIP\"");
                               String value = inClientXML.checkValue(reader);
                               if (value.equals("OK") || value.equals("GAME STARTED, you are PLAYER ONE") ||
                                       value.equals("GAME STARTED, you are PLAYER TWO")) {

                                   Platform.runLater(new Runnable() {
                                       @Override
                                       public void run() {
                                           gameController.setShip();
                                       }
                                   });
                                   break;
                               } else {
                                   showDialogInfo("SHIP LOCATION", value);

                               }
                               break;
                           }
                           case "SHOOT RESULT": {
                               System.out.println("\n\n\nSERVER:\"SHOOT\"");
                               String value = inClientXML.checkValue(reader);

                               Platform.runLater(new Runnable() {
                                   @Override
                                   public void run() {
                                       gameController.setShoot(value);
                                   }
                               });


                               showDialogInfo("SHIP LOCATION", value);
                               break;
                           }
                               //сообщение сервера о результате выстрела (ранил / убил / мимо)

                           case "SURRENDER": {
                               System.out.println("\n\n\nSERVER:\"SURRENDER\"");
                               commonWindowController.setEnemySurrender(true);
                               break;
                           }
                           case "GAME OVER": {
                               System.out.println("\n\n\nSERVER:\"GAME OVER\"");
                               //сообщение сервера об окончании игры и выводе результата
                               break;
                           }
                           case "INFO": {
                               System.out.println("\n\n\nSERVER:\"INFO\"");
                               String value = inClientXML.checkValue(reader);
                               System.out.println("***\"" + value + "\"***");
                               showDialogInfo("SERVER INFO", value);
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

       /* if(outClientXML.getWriter() != null) {
                try {
                    outClientXML.getWriter().close();
                    outClientXML.getWriter2().close();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        System.out.println("RUN IS ENDED");

    }

    private void showDialogInfo(String key, String value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                DialogManager.showInfoDialog(key, value);
            }
        });
    }

    public void disconnect(){
        System.out.println("key: LOG OUT " + "value " + username);
        try {
            outClientXML.send("LOG OUT", username);
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
        isConnect = false;
            System.out.println("Server is disconnected");
    }
    }

    public String getEnemy() {
        return enemy;
    }

    public OutClientXML getOutClientXML() {
        return outClientXML;
    }

    public InClientXML getInClientXML() {
        return inClientXML;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setIsConnect(boolean isConnect) {
        this.isConnect = isConnect;
    }

    public  String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCommonWindowController(CommonWindowController commonWindowController) {
        this.commonWindowController = commonWindowController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public Thread getTh() {
        return th;
    }
}