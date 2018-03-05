package client.controller;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import client.xmlservice.InClientXML;
import client.xmlservice.OutClientXML;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


public class ServerListener implements Runnable{

    final static Logger logger = Logger.getLogger(ServerListener.class);


    private static OutClientXML outClientXML;
    private String password;
    private Socket socket;
    private String hostname;
    private int port;
    private static String username;
    private  RegController regController;
    private static InClientXML inClientXML;
    private Scanner scanner;
    private String key;
    private static boolean isConnect;
    private static String enemy;


    public ServerListener(String hostname, int port, String username, String password, String key) {
        this.hostname = hostname;
        this.port = port;
        this.password = password;
        this.username = username;
        this.key = key;
        new Thread(this).start();
    }

    @Override
    public void run() {
        regController = RegController.getRegController();
        try (Socket socket = new Socket(hostname, port)){
            this.socket = socket;
            isConnect = true;
            inClientXML = new InClientXML(socket);
            outClientXML = new OutClientXML(socket);
            outClientXML.send(key, username, password);
            //console check
            this.scanner = new Scanner(System.in);
            inClientXML.setReader(inClientXML.getFactory().createXMLStreamReader(inClientXML.getFileReader()));
            XMLStreamReader reader = inClientXML.getReader();
            while (isConnect()) {
                while (reader.hasNext()) {
                    if (reader.getEventType() == 1 && reader.getLocalName().equals("key")) {
                        reader.next();
                        switch (inClientXML.checkValue(reader)) {
                            case "LOG IN": {
                                System.out.println("\nxml message with key \"LOG IN\" received:");
                                String value = inClientXML.checkValue(reader);
                                System.out.println("LOG IN server answer = \"" + value + "\"");

                                if (value.equals("success!")){
                                    regController.showCommonWindow();
                                    break;
                                }

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
                                if (value.equals("success!")){
                                    regController.showCommonWindow();
                                } else {
                                    regController.getInputStatus().setText("Status:" + value);
                                }

                            }
                            case "MSG": {
                                //делать в последнюю очередь
                                System.out.println("xml message with key \"MSG\" received");
                                break;
                            }
                            case "INVITE": {
                                System.out.println("xml message with key \"INVITE\" received");
                                //сообщение от сервера о том что игрок №n предлагает игру
                                break;
                            }
                            case "REPLY": {
                                System.out.println("xml message with key \"REPLY\" received");
                                //сообщение от сервера о том что ответ игрока №n на ваше предложение игры - ...
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
            }
        } catch (IOException e) {
            RegController.getRegController().showErrorDialog("Could not connect to server");
            logger.error("Could not Connect" + socket.getInetAddress() + ":" + socket.getPort());


        } catch (XMLStreamException e) {
            logger.error("XMLStreamException in ServerListener thread", e);
        }
        logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
    }

    public static String getEnemy() {
        return enemy;
    }

    public static OutClientXML getOutClientXML() {
        return outClientXML;
    }

    public static InClientXML getInClientXML() {
        return inClientXML;
    }

    public static boolean isConnect() {
        return isConnect;
    }

    public static void setIsConnect(boolean isConnect) {
        ServerListener.isConnect = isConnect;
    }

    public static String getUsername() {
        return username;
    }

}
