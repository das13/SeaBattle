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
    private Player thisPlayer;
    private GameController gc;
    private boolean waitingForReply;

    public OutServerXML getOutServerXML() {
        return outServerXML;
    }

    public Player getThisPlayer() {
        return thisPlayer;
    }

    public void setThisPlayer(Player thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    public PlayerController() {
        thisPlayer = new Player();
    }

    public PlayerController(Socket socket) throws IOException {
        thisPlayer = new Player();
        this.socket = socket;
        inServerXML = new InServerXML(socket);
        outServerXML = new OutServerXML(socket);
    }



    public void run() {
        threadNumber = Server.getCountOfThread();
        try {
            while (!socket.isClosed()){
                inServerXML.setReader(inServerXML.getFactory().createXMLStreamReader(inServerXML.getFileReader()));
                XMLStreamReader reader = inServerXML.getReader();
                while (reader.hasNext()) {
                    if (reader.getEventType() == 1 && reader.getLocalName().equals("key")){
                        reader.next();
                        switch (inServerXML.checkValue(reader)){
                            case "LOG IN": {
                                String login = inServerXML.checkValue(reader);
                                thisPlayer.setLogin(login);
                                System.out.println("\n\n\nkey \"LOG IN\" from " + this.getThisPlayer().getLogin() + " detected:");
                                System.out.println("login = \"" + login + "\"");
                                String password = inServerXML.checkValue(reader);
                                thisPlayer.setPassword(password);
                                System.out.println("password = \"" + password + "\"" + "\nSENDING ANSWER:");
                                outServerXML.send("INFO", authResult(login,password));
                                Server.updateAllPlayersSet();
                                Server.updateOnlinePlayersSet();
                                break;
                            }
                            case "LOG OUT": {
                                System.out.println("\n\n\n\nkey \"LOG OUT\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String login = inServerXML.checkValue(reader);
                                outServerXML.send("INFO",logoutResult(login));
                                Server.updateAllPlayersSet();
                                Server.updateOnlinePlayersSet();
                                break;
                            }
                            case "REG": {
                                System.out.println("\n\n\n\nkey \"REG\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String login = inServerXML.checkValue(reader);
                                System.out.println("login = \"" + login + "\"");
                                String password = inServerXML.checkValue(reader);
                                System.out.println("password = \"" + password + "\"" + "\nSENDING ANSWER:");
                                outServerXML.send("INFO", regResult(login,password));
                                Server.updateAllPlayersSet();
                                break;
                            }
                            case "MSG": {
                                System.out.println("\n\n\nkey \"MSG\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player = inServerXML.checkValue(reader);
                                System.out.println("player = \"" + player + "\"");
                                String msg = inServerXML.checkValue(reader);
                                System.out.println("msg = \"" + msg + "\"");
                                msgResult(player,msg);
                                break;
                            }
                            case "INVITE": {
                                waitingForReply = true;
                                System.out.println("\n\n\nkey \"INVITE\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player1 = inServerXML.checkValue(reader);
                                System.out.println("player1 = \"" + player1 + "\"");
                                String player2 = inServerXML.checkValue(reader);
                                System.out.println("player2 = \"" + player2 + "\"");
                                inviteResult(player1,player2);
                                break;
                            }
                            //можно получить ТОЛЬКО от player2 (от того, кого пригласили)
                            case "REPLY": {
                                System.out.println("\n\n\nkey \"REPLY\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player1 = inServerXML.checkValue(reader);
                                System.out.println("player1 = \"" + player1 + "\"");
                                String reply = inServerXML.checkValue(reader);
                                System.out.println("reply = \"" + reply + "\"");
                                replyResult(player1,reply);
                                break;
                            }
                            case "SHIP LOCATION": {
                                System.out.println("\n\n\nkey \"SHIP LOCATION\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player = inServerXML.checkValue(reader);
                                System.out.println("player = \"" + player + "\"");
                                String x1 = inServerXML.checkValue(reader);
                                System.out.println("x1 = \"" + x1 + "\"");
                                String y1 = inServerXML.checkValue(reader);
                                System.out.println("y1 = \"" + y1 + "\"");
                                String x2 = inServerXML.checkValue(reader);
                                System.out.println("x2 = \"" + x2 + "\"");
                                String y2 = inServerXML.checkValue(reader);
                                System.out.println("y2 = \"" + y2 + "\"");
                                shipLocationResult(player,x1,y1,x2,y2);
                                break;
                            }
                            case "SHOOT": {
                                System.out.println("\n\n\nkey \"SHOOT\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player = inServerXML.checkValue(reader);
                                System.out.println("player = \"" + player + "\"");
                                String x1 = inServerXML.checkValue(reader);
                                System.out.println("x1 = \"" + x1 + "\"");
                                String y1 = inServerXML.checkValue(reader);
                                System.out.println("y1 = \"" + y1 + "\"");
                                String x2 = inServerXML.checkValue(reader);
                                System.out.println("x2 = \"" + x2 + "\"");
                                String y2 = inServerXML.checkValue(reader);
                                System.out.println("y2 = \"" + y2 + "\"");
                                shootResult(player, x1,y1,x2,y2);
                                break;
                            }
                            case "SURRENDER": {
                                System.out.println("\n\n\nkey \"SURRENDER\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player = inServerXML.checkValue(reader);
                                System.out.println("player = \"" + player + "\"");
                                surrenderResult(player);
                                break;
                            }
                            case "GAME OVER": {
                                //это сервер должен определить когда отсылать гейм овер
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

    //действие на приглашение игроком№1 игрока№2 в игру
    private void inviteResult(String player1, String player2) {
        for (PlayerController pl:Server.getAllPlayersControllerSet()) {
            if (pl.getThisPlayer().getLogin().equals(player2)) {
                pl.getOutServerXML().send("INVITE",player1);
            }
        }
        outServerXML.send("INFO","invite send to player: " + player2);
    }

    //действие на ответ игрока№2 игроку№1 на приглашение в игру
    private void replyResult(String player1, String value) {

        for (PlayerController pc : Server.getAllPlayersControllerSet()) {
            if (pc.getThisPlayer().getLogin().equals(player1)) {
                if (pc.isWaitingForReply()) {
                    gc = new GameController(pc, this);
                    pc.setGc(gc);
                    gc.start();
                    pc.getOutServerXML().send("START GAME", thisPlayer.getLogin());
                    System.out.print("\n");
                    outServerXML.send("START GAME", player1);
                    break;
                }
            }
            System.out.println("ERROR! dude " + pc + " don't wanna play!");
        }
    }

    //действие на сообщение
    private void msgResult(String login, String msg) {
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

        for (Player player : tempSet){
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

    private void surrenderResult(String player) {
    }

    private void shootResult(String player, String x1, String y1, String x2, String y2) {
    }

    private void shipLocationResult(String player, String x1, String y1, String x2, String y2) {
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

    //getters and setters


    public boolean isWaitingForReply() {
        return waitingForReply;
    }

    public GameController getGc() {
        return gc;
    }

    public void setGc(GameController gc) {
        this.gc = gc;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        inServerXML = new InServerXML(socket);
        outServerXML = new OutServerXML(socket);
    }
}
