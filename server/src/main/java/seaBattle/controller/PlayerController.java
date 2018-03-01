package seaBattle.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import seaBattle.model.Player;
import seaBattle.model.Server;
import seaBattle.xmlservice.InServerXML;
import seaBattle.xmlservice.OutServerXML;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.util.Iterator;

import static seaBattle.model.Server.players;

public class PlayerController extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int threadNumber;
    private static final String PLAYERLIST = "playerList.xml";
    private String str;
    private InServerXML inServerXML;
    private OutServerXML outServerXML;
    private Player player;

    public PlayerController(Socket socket) throws IOException {
        player = new Player();
        this.socket = socket;
        inServerXML = new InServerXML(socket);
        outServerXML = new OutServerXML(socket);
    }

    public void run() {
        threadNumber = Server.getCountOfThread();
        try {
            while (!socket.isClosed()){
                System.out.println("\n/receiving from thread #" + threadNumber);
                //for XML data receiving
                inServerXML.setReader(inServerXML.getFactory().createXMLStreamReader(inServerXML.getFileReader()));
                XMLStreamReader reader = inServerXML.getReader();
                while (reader.hasNext()) {
                    if (reader.getEventType() == 1 && reader.getLocalName().equals("key")){
                        reader.next();
                        switch (inServerXML.checkValue(reader)){
                            case "AUTH": {
                                System.out.println("\nxml message with key \"AUTH\" from thread #" + threadNumber + " detected:");
                                player.setLogin(inServerXML.checkValue(reader));
                                System.out.println("login = \"" + player.getLogin() + "\"");
                                player.setPassword(inServerXML.checkValue(reader));
                                System.out.println("password = \"" + player.getPassword() + "\"" + "\n\nSENDING ANSWER:");
                                outServerXML.send("AUTH", authResult(player.getLogin(),player.getPassword()));
                                break;
                            }
                            case "REG":
                                System.out.println("\nxml message with key \"REG\" from thread #" + threadNumber + " detected:");
                                String login = inServerXML.checkValue(reader);
                                System.out.println("login = \"" + login + "\"");
                                String password = inServerXML.checkValue(reader);
                                System.out.println("password = \"" + password + "\"" + "\n\nSENDING ANSWER:");
                                outServerXML.send("REG", regResult(login,password));
                                break;
                            case "MSG":
                                System.out.println("xml message with key \"MSG\" detected");
                                break;
                            case "ASK OUT":
                                GameController gameController = new GameController(player,this);
                                System.out.println("\nWrite nickname of target:");
                                String targetLogin = inServerXML.checkValue(reader);
                                outServerXML.send("ASK OUT", askOutResult(targetLogin));
                                Iterator itr = players.iterator();
                                Player targetPlayer = null;
                                while (itr.hasNext()) {
                                    Player pl = (Player) itr.next();
                                    if (pl.getLogin().equals(targetLogin)) {
                                        targetPlayer = pl;
                                        break;
                                    }
                                }
                                gameController.startGame(targetPlayer);
                                //System.out.println("xml message with key \"ASK OUT\" detected");
                                break;
                            case "ASK ANSWER":
                                System.out.println("xml message with key \"ASK ANSWER\" detected");
                                break;
                            case "SHIP":
                                System.out.println("xml message with key \"SHIP LOCATION\" detected");
                                break;
                            case "READY":
                                System.out.println("xml message with key \"READY\" detected");
                                break;
                            case "SHOOT":
                                System.out.println("xml message with key \"SHOOT\" detected");
                                break;
                            case "SURRENDER":
                                System.out.println("xml message with key \"SURRENDER\" detected");
                                break;
                            case "GAME OVER":
                                System.out.println("xml message with key \"GAME OVER\" detected");
                        }
                    }
                    if (reader.isEndElement() && "root".equals(reader.getName().toString())) {
                        break;
                    }else{
                        reader.next();
                    }
                }
                reader.close();
            }
        } catch (XMLStreamException | SAXException | ParserConfigurationException | IOException | TransformerException e) {
            e.printStackTrace();
        } finally {

            if (player.getLogin() != null) {
                Server.getNames().remove(player.getLogin());
            }
            if (out != null) {
                Server.getWriters().remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    
    public String authResult(String login, String password) throws ParserConfigurationException, IOException, SAXException, TransformerException, XMLStreamException {

        // Строим объектную модель исходного XML файла
        final String filepath = System.getProperty("user.dir") + File.separator + PLAYERLIST;
        final File xmlFile = new File(filepath);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(xmlFile);
        doc.normalize();

        // бежим по каждому player в players (playerList.xml)
        NodeList players = doc.getElementsByTagName("player");
        for (int i = 0; i < players.getLength(); i++) {

            Node node = players.item(i);
            //если указанные имя и пароль не найдены в списке
            if (!node.getChildNodes().item(1).getTextContent().equals(login) && !node.getChildNodes().item(3).getTextContent().equals(password)){
                str = "account with such login or password is not exist";
            }

            //если совпадает имя или пароль
            if ((node.getChildNodes().item(1).getTextContent().equals(login) && !node.getChildNodes().item(3).getTextContent().equals(password)) ||
                    (!node.getChildNodes().item(1).getTextContent().equals(login) && node.getChildNodes().item(3).getTextContent().equals(password))){
                str = "account or password is incorrect";
                break;
            }
            //если совпадает и имя и пароль
            if (node.getChildNodes().item(1).getTextContent().equals(login) && node.getChildNodes().item(3).getTextContent().equals(password)) {
                // изменение статуса на online
                node.getChildNodes().item(5).setTextContent("online");

                // Записываем изменения в XML файл
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(filepath));
                transformer.transform(source, result);

                str = "authorization success!";
                break;
            }
        }
        // Сообщение клиенту о результате авторизации
        return str;
    }

    public String regResult(String value1, String value2) throws ParserConfigurationException, IOException, SAXException {
        final String filepath = System.getProperty("user.dir") + File.separator + PLAYERLIST;
        final File xmlFile = new File(filepath);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(xmlFile);
        doc.normalize();
        //check nickname match
        NodeList players = doc.getElementsByTagName("player");
        for (int i = 0; i < players.getLength(); i++) {
            Node node = players.item(i);
            if (node.getChildNodes().item(1).getTextContent().equals(value1)){
                str = "error. player with nickname " + value1 + " already exist.";
                return str;
            }
        }

        Element player = doc.createElement("player");
        player.setAttribute("id","4");
        doc.getFirstChild().appendChild(player);
        Element nickname = doc.createElement("nickname");
        nickname.setTextContent(value1);
        player.appendChild(nickname);
        Element password = doc.createElement("password");
        password.setTextContent(value2);
        player.appendChild(password);
        Element status = doc.createElement("status");
        status.setTextContent("offline");
        player.appendChild(status);
        Element rating = doc.createElement("rating");
        rating.setTextContent("100");
        player.appendChild(rating);

        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filepath));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        System.out.println("new player registered");
        str = "registration success";
        return str;
    }

    public String askOutResult(String value) {
        return str;
    }
}
