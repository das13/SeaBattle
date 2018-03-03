package seaBattle.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import seaBattle.model.BSException;
import seaBattle.model.Player;
import seaBattle.model.PlayerList;
import seaBattle.model.Server;
import seaBattle.xmlservice.InServerXML;
import seaBattle.xmlservice.OutServerXML;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PlayerController extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int threadNumber;
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
                System.out.println("\n\n/receiving from thread #" + threadNumber);
                //for XML data receiving
                inServerXML.setReader(inServerXML.getFactory().createXMLStreamReader(inServerXML.getFileReader()));
                XMLStreamReader reader = inServerXML.getReader();
                while (reader.hasNext()) {
                    if (reader.getEventType() == 1 && reader.getLocalName().equals("key")){
                        reader.next();
                        switch (inServerXML.checkValue(reader)){
                            case "AUTH": {
                                System.out.println("\nxml message with key \"AUTH\" from thread #" + threadNumber + " detected:");
                                String login = inServerXML.checkValue(reader);
                                System.out.println("login = \"" + login + "\"");
                                String password = inServerXML.checkValue(reader);
                                System.out.println("password = \"" + password + "\"" + "\n\nSENDING ANSWER:");
                                outServerXML.send("AUTH", authResult(login,password));
                                Server.updateAllPlayersSet();
                                Server.updateOnlinePlayersSet();
                                break;
                            }
                            case "REG": {
                                System.out.println("\nxml message with key \"REG\" from thread #" + threadNumber + " detected:");
                                String login = inServerXML.checkValue(reader);
                                System.out.println("login = \"" + login + "\"");
                                String password = inServerXML.checkValue(reader);
                                System.out.println("password = \"" + password + "\"" + "\n\nSENDING ANSWER:");
                                outServerXML.send("REG", regResult(login,password));
                                Server.updateAllPlayersSet();
                                break;
                            }
                            case "LOGOUT": {
                                System.out.println("xml message with key \"LOGOUT\" detected");
                                String login = inServerXML.checkValue(reader);
                                outServerXML.send("LOGOUT",logoutResult(login));
                                Server.updateAllPlayersSet();
                                Server.updateOnlinePlayersSet();
                                break;
                            }
                            case "MSG": {
                                System.out.println("xml message with key \"MSG\" detected");
                                break;
                            }
                            case "INVITE": {
                                System.out.println("xml message with key \"ASK OUT\" detected");
                                break;
                            }
                            case "REPLY": {
                                System.out.println("xml message with key \"ASK ANSWER\" detected");
                                break;
                            }
                            case "SHIP": {
                                System.out.println("xml message with key \"SHIP LOCATION\" detected");
                                break;
                            }
                            case "SHOOT": {
                                System.out.println("xml message with key \"SHOOT\" detected");
                                break;
                            }
                            case "SURRENDER": {
                                System.out.println("xml message with key \"SURRENDER\" detected");
                                break;
                            }
                            case "GAMEOVER": {
                                System.out.println("xml message with key \"GAME OVER\" detected");
                            }
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
        } catch (XMLStreamException | SAXException | ParserConfigurationException | IOException | TransformerException | BSException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    public String authResult(String login, String password) throws ParserConfigurationException, IOException, SAXException, TransformerException, XMLStreamException, BSException {
        for (Player player : Server.getAllPlayersSet()){
            if (player.getLogin().equals(login) && player.getPassword().equals(password)){
                str = "player with this login or password not found. register first";
            }
            if (player.getLogin().equals(login) && player.getStatus().equals("online")){
                str = "player with nickname \"" + login + "\" already logged in";
                System.out.println("RESULT = " + str);
                return str;
            }
            if (!player.getLogin().equals(login) && player.getPassword().equals(password) || player.getLogin().equals(login) && !player.getPassword().equals(password)){
                str = "login or password is incorrect";
                System.out.println("RESULT = " + str);
                return str;
            }
            if (player.getLogin().equals(login) && player.getPassword().equals(password)){
                modifyPlayerInXML(Server.getPlayerListXML(),player,"status", "online");
                str = "success!";
                System.out.println("RESULT = " + str);
                return str;
            }
        }
        System.out.println("RESULT = " + str);
        return str;
    }

    public String regResult(String login, String password){

        PlayerList playerList = new PlayerList();

        playerList.setPlayerList(new ArrayList<Player>());
        Set<Player> tempSet = Server.getAllPlayersSet();

        System.out.println("SET SIZE = " + Server.getAllPlayersSet().size());
        System.out.println("tempSET SIZE = " + tempSet.size());

        for (Player player : tempSet){
            System.out.println("ADDING INTO PLAYERLIST - " + player.getLogin());
            playerList.getPlayerList().add(player);
        }

        Player p1 = new Player();
        p1.setLogin(login);
        p1.setPassword(password);
        p1.setStatus("offline");
        p1.setRank(100);

        playerList.getPlayerList().add(p1);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PlayerList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //Marshal-им плеерлист в консоль
            jaxbMarshaller.marshal(playerList, System.out);


            //Marshal-им плеерлист в файл
            jaxbMarshaller.marshal(playerList, new File(Server.getPlayerListXML().getPath()));
            str = "success!";

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String logoutResult(String login) throws BSException, ParserConfigurationException, TransformerException, SAXException, IOException {
        for (Player player : Server.getOnlinePlayersSet()){
            if (player.getLogin().equals(login) && player.getStatus().equals("online")){
                modifyPlayerInXML(Server.getPlayerListXML(),player,"status","offline");
                str = "success!";
                return str;
            }
        }
        str = "error, you already logged out";
        //throw new BSException("Error with logging out player");
        return str;
    }

    public void modifyPlayerInXML (File file, Player player, String nodeName, String newTextContent) throws ParserConfigurationException, IOException, SAXException, TransformerException, BSException {
        // Строим объектную модель исходного XML файла
        final String filepath = System.getProperty("user.dir") + File.separator + file;
        final File xmlFile = new File(filepath);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(xmlFile);
        doc.normalize();

        // бежим по каждому player в xml
        NodeList players = doc.getElementsByTagName("player");
        for (int i = 0; i < players.getLength(); i++) {
            Node node = players.item(i);
            //если совпадает и имя и пароль
            if (node.getChildNodes().item(1).getTextContent().equals(player.getLogin())){
                for (int j = 0; j < node.getChildNodes().getLength(); j++){
                    if (node.getChildNodes().item(j).getNodeName().equals(nodeName)){
                        node.getChildNodes().item(j).setTextContent(newTextContent);
                    }
                }
                // Записываем изменения в XML файл
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(filepath));
                transformer.transform(source, result);

                System.out.println("MODIFY XML SUCCESS. In: " + file + ", player: " + player.getLogin() + ", value: " + nodeName + ", new text in value: " + newTextContent);
                break;
            }
        }
        //throw new BSException("Error with finding player in XML");
    }

    public void saveSetIntoXML(HashSet<Player> players, File file){

    }
}
