package client;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import client.xmlservice.InClientXML;
import client.xmlservice.OutClientXML;

public class ClientTEST {
    private InClientXML inClientXML;
    private OutClientXML outClientXML;
    private Scanner scanner;
    private Socket socket;
    private String login;

    public ClientTEST() throws IOException, XMLStreamException {
        this.socket = new Socket("localhost", 9001);
        inClientXML = new InClientXML(socket);
        outClientXML = new OutClientXML(socket);
        //console check
        this.scanner = new Scanner(System.in);
        new ClientTESTThread().start();
    }

    public static void main(String[] args) throws  Exception{
        ClientTEST clientTEST = new ClientTEST();
        clientTEST.run();
    }

    public void run() throws IOException, XMLStreamException, InterruptedException {
        System.out.println("THE CLIENT IS RUNNING\n\nBuild your messages in this way:\n" +
                "1.key (\"LOG IN\", \"LOG OUT\", \"REG\", \"INVITE\", \"REPLY\" etc.)\n" +
                "2.value1 ...\n" +
                "3.value2 ...\n\n" +
                "now try to write something below, start with \"key\":");
        while (!socket.isClosed()){
            String key = scanner.nextLine();
            System.out.println("write value1:");
            String value1 = scanner.nextLine();
            if(key.equals("LOG IN")) login = value1.toString();
            System.out.println("write value2:");
            String value2 = scanner.nextLine();
            System.out.println("SENDING:");
            outClientXML.send(key,value1,value2);
        }
        System.out.println("\n\nexit");
        outClientXML.getWriter().close();
        outClientXML.getWriter2().close();
    }

    class ClientTESTThread extends Thread{

        @Override
        public void run() {
            while (!socket.isClosed()){
                try {
                    inClientXML.setReader(inClientXML.getFactory().createXMLStreamReader(inClientXML.getFileReader()));
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
                XMLStreamReader reader = inClientXML.getReader();
                try {
                    while (reader.hasNext()) {
                        if (reader.getEventType() == 1 && reader.getLocalName().equals("key")) {
                            reader.next();
                            switch (inClientXML.checkValue(reader)) {
                                case "LOG IN": {
                                    System.out.println("\n\n\nSERVER:\"LOG IN\"");
                                    String value = inClientXML.checkValue(reader);
                                    System.out.println("result = \"" + value + "\"");
                                    //реакция в зависимости от ответа. да - ..., ошибка№1 нет такого ни логина ни пароля - ...,
                                    //ошибка№2 пароль или логин не верен - ...
                                    break;
                                }
                                case "LOG OUT": {
                                    System.out.println("\n\n\nSERVER:\"LOG OUT\"");
                                    String value = inClientXML.checkValue(reader);
                                    System.out.println("result = \"" + value + "\"");
                                    //если ответ сервера удовлетворителен - ...
                                    break;
                                }
                                case "REG": {
                                    System.out.println("\n\n\nSERVER:\"REG\"");
                                    String value = inClientXML.checkValue(reader);
                                    System.out.println("result = \"" + value + "\"");
                                    //если ответ сервера - ...
                                    break;
                                }
                                case "MSG": {
                                    //делать в последнюю очередь
                                    System.out.println("\n\n\nSERVER:\"MSG\"");
                                    break;
                                }
                                case "INVITE": {
                                    System.out.println("\n\n\nSERVER:\"INVITE\"");
                                    String player1 = inClientXML.checkValue(reader);
                                    System.out.println("player \"" + player1 + "\" want to play with you. Use key \" REPLY\" to reply. " +
                                            "value1 must be " + player1 + ", and value2 is your answer (now it is \"AUTOAGGREE\" MODE)");
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
                                    break;
                                }
                                case "ONLINE PLAYERS": {
                                    System.out.println("\n\n\nSERVER:\"ONLINE PLAYERS\"");
                                    int countOfPlayers = Integer.parseInt(inClientXML.checkValue(reader));
                                    for (int i = 0; i < countOfPlayers; i++){
                                        System.out.println("online player#" + i + " - "+ inClientXML.checkValue(reader));
                                    }
                                    break;
                                }
                                case "INGAME PLAYERS": {
                                    System.out.println("\n\n\nSERVER:\"INGAME PLAYERS\"");
                                    int countOfPlayers = Integer.parseInt(inClientXML.checkValue(reader));
                                    for (int i = 0; i < countOfPlayers; i++){
                                        System.out.println("online player#" + i + " - "+ inClientXML.checkValue(reader));
                                    }
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
                                case "SHOOT": {
                                    System.out.println("\n\n\nSERVER:\"SHOOT\"");
                                    //сообщение сервера о результате выстрела (ранил / убил / мимо)
                                    break;
                                }
                                case "SURRENDER": {
                                    System.out.println("\n\n\nSERVER:\"SURRENDER\"");
                                    //сообщение сервера о том что соперник сдался
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
                                    break;
                                }
                            }
                        }
                        if (reader.isEndElement() && "root".equals(reader.getName().toString())) {
                            break;
                        }else{
                            reader.next();
                        }
                    }
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


