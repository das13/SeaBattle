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

    private static final String HASCONNECTED = "has connected";
    final static Logger logger = Logger.getLogger(ServerListener.class);
    private OutClientXML outClientXML;
    private String password;
    private Socket socket;
    private String hostname;
    private int port;
    private static String username;
    private CommonWindowController cwController;
    private RegController regController;
    private static ObjectOutputStream oos;
    private InputStream is;
    private ObjectInputStream input;
    private OutputStream outputStream;
    private InClientXML inClientXML;
    private Scanner scanner;
    private String key;



    public ServerListener(String hostname, int port, String username, String password, CommonWindowController controller, String key) {
        this.hostname = hostname;
        this.port = port;
        this.password = password;
        ServerListener.username = username;
        this.cwController = cwController;
        this.key = key;
    }

    @Override
    public void run() {
        regController = RegController.getRegController();
        try (Socket socket = new Socket(hostname, 9001)){
            this.socket = socket;
            inClientXML = new InClientXML(socket);
            outClientXML = new OutClientXML(socket);
            outClientXML.send(key, username, password);
            //console check
            this.scanner = new Scanner(System.in);
            inClientXML.setReader(inClientXML.getFactory().createXMLStreamReader(inClientXML.getFileReader()));
            XMLStreamReader reader = inClientXML.getReader();
            while (reader.hasNext()) {
                String key = inClientXML.checkValue(reader);
                String value = inClientXML.checkValue(reader);
                System.out.println("key = \"" + key +"\"");
                System.out.println("value = \"" + value +"\"");
                if (value.equals("registration success")){
                    regController.showCommonWindow();
                    break;
                }
                if (value.equals("authorization success!")){
                    regController.showCommonWindow();
                    break;
                }
                reader.next();
                reader.next();
                                //пока считывает один value
                                //inClientXML.checkValue(reader);
                                //inClientXML.printEvent(reader);

                if (reader.isEndElement() && "root".equals(reader.getName().toString())) {
                    break;
                }else{
                    reader.next();
                }
                //            while (true) {
                //                while (true){
                //                    System.out.println("idle loop");
                //                    условие выхода с цикла ожидания
                //                    break;
                //                }
                //                while (true) {
                //                    System.out.println("game loop");
                //                    условие выхода с цикла игры
                //                    break;
                //                }
            }



            //
        } catch (IOException e) {
            RegController.getRegController().showErrorDialog("Could not connect to server");
            logger.error("Could not Connect" + socket.getInetAddress() + ":" + socket.getPort());

        } catch (XMLStreamException e) {
            logger.error("XMLStreamException in ServerListener thread", e);
        }
        logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
    }
}
