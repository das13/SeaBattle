package client.controller;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import client.xmlservice.InClientXML;
import client.xmlservice.OutClientXML;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLStreamException;


public class ServerListener implements Runnable{

    private static final String HASCONNECTED = "has connected";
    final static Logger logger = Logger.getLogger(ServerListener.class);
    private OutClientXML outClientXML;
    private String password;
    private Socket socket;
    public String hostname;
    public int port;
    public static String username;
    public CommonWindowController cwController;
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
        try (Socket socket = new Socket(hostname, 9001)){
            this.socket = socket;
            inClientXML = new InClientXML(socket);
            outClientXML = new OutClientXML(socket);
            outClientXML.send(key, username, password);
            //console check
            this.scanner = new Scanner(System.in);

            RegController.getRegController().showCommonWindow();
           // cwController.getCwController().getLblLogin().setText(username);
        } catch (IOException e) {
            RegController.getRegController().showErrorDialog("Could not connect to server");
            logger.error("Could not Connect" + socket.getInetAddress() + ":" + socket.getPort());

        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
    }
}
