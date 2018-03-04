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

    synchronized public void run() throws IOException, XMLStreamException, InterruptedException {
        while (!socket.isClosed()){
            System.out.println("\nwrite key");
            String key = scanner.nextLine();
            System.out.println("write value1:");
            String value1 = scanner.nextLine();
            if(key.equals("LOG IN")) login = value1.toString();
            System.out.println("write value2:");
            String value2 = scanner.nextLine();
            System.out.println("SENDING:");
            outClientXML.send(key,value1,value2);
            System.out.println("\n\nSERVER THREAD ANSWER:");

//            inClientXML.setReader(inClientXML.getFactory().createXMLStreamReader(inClientXML.getFileReader()));
//            XMLStreamReader reader = inClientXML.getReader();
//            while (reader.hasNext()) {
//                System.out.println("key = \"" + inClientXML.checkValue(reader) +"\"");
//                System.out.println("value = \"" + inClientXML.checkValue(reader) +"\"");
//                reader.next();
//                if (reader.isEndElement() && "root".equals(reader.getName().toString())) {
//                    break;
//                }else{
//                    reader.next();
//                }
//            }
        }
        System.out.println("\n\nexit");
        outClientXML.getWriter().close();
        outClientXML.getWriter2().close();
    }

    class ClientTESTThread extends Thread{

        @Override
        synchronized public void run() {
            while (!socket.isClosed()){
                try {
                    inClientXML.setReader(inClientXML.getFactory().createXMLStreamReader(inClientXML.getFileReader()));
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
                XMLStreamReader reader = inClientXML.getReader();
                try {
                    while (reader.hasNext()) {
                        /*System.out.println("key = \"" + inClientXML.checkValue(reader) +"\"");
                        System.out.println("value = \"" + inClientXML.checkValue(reader) +"\"");
                        reader.next();
                        if (reader.isEndElement() && "root".equals(reader.getName().toString())) {
                            break;
                        }else{
                            reader.next();
                        }*/
                        if (reader.getEventType() == 1 && reader.getLocalName().equals("key")) {
                            reader.next();
                            switch (inClientXML.checkValue(reader)) {
                                case "LOG IN": {
                                    System.out.println("\nxml message with key \"LOG IN\" received:");
                                    String value = inClientXML.checkValue(reader);
                                    System.out.println("LOG IN server answer = \"" + value + "\"");
                                    //реакция в зависимости от ответа. да - ..., ошибка№1 нет такого ни логина ни пароля - ...,
                                    //ошибка№2 пароль или логин не верен - ...
                                    break;
                                }
                                case "LOG OUT": {
                                    System.out.println("xml message with key \"LOG OUT\" received");
                                    String value = inClientXML.checkValue(reader);
                                    System.out.println("LOG OUT server answer = \"" + value + "\"");
                                    //если ответ сервера удовлетворителен - ...
                                    break;
                                }
                                case "REG": {
                                    System.out.println("xml message with key \"REG\" received");
                                    String value = inClientXML.checkValue(reader);
                                    System.out.println("REG server answer = \"" + value + "\"");
                                    //если ответ сервера - ...
                                    break;
                                }
                                case "MSG": {
                                    //делать в последнюю очередь
                                    System.out.println("xml message with key \"MSG\" received");
                                    break;
                                }
                                case "INVITE": {
                                    System.out.println("xml message with key \"INVITE\" received");
                                    String player1 = inClientXML.checkValue(reader);
                                    System.out.println("value = \"" + player1 + "\"");
                                    //сделать на клиенте
//                                    //первый тот кто отвечает - второй тот кому отвечает первый - то что отвечает первый второму
//                                    String[] list = new String[0];
//                                    list[0] = login;
//                                    list[1] = player1;
//                                    list[2] = valueReq;
//                                    outClientXML.send("REPLY",list);
                                    //сообщение от сервера о том что игрок №n предлагает игру
                                    break;
                                }
                                case "REPLY": {
                                    System.out.println("xml message with key \"REPLY\" received");
                                    //сообщение от сервера о том что ответ игрока №n на ваше предложение игры - ...
                                    break;
                                }
                                case "START GAME": {
                                    System.out.println("\nxml message with key \"START GAME\" received:");
                                    String value = inClientXML.checkValue(reader);
                                    System.out.println("GAME STARTED WITH = \"" + value + "\"");
                                    break;
                                }
                                case "SHIP": {
                                    System.out.println("xml message with key \"SHIP\" received");
                                    //сообщение от сервера относительно того как игрок пытается поставить корабль - ...
                                    //(успех / ошибка / все корабли расставлены и идёт ожидание другого игрока или запуск игры)
                                    break;
                                }
                                case "SHOOT": {
                                    System.out.println("xml message with key \"SHOOT\" received");
                                    //сообщение сервера о результате выстрела (ранил / убил / мимо)
                                    break;
                                }
                                case "SURRENDER": {
                                    System.out.println("xml message with key \"SURRENDER\" received");
                                    //сообщение сервера о том что соперник сдался
                                    break;
                                }
                                case "GAME OVER": {
                                    System.out.println("xml message with key \"GAME OVER\" received");
                                    //сообщение сервера об окончании игры и выводе результата
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


