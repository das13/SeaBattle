package seaBattle.model;

import seaBattle.controller.PlayerController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.net.ServerSocket;
import java.util.*;

public class Server {

    private static File playerListXML = new File("playerList.xml");
    private static File ipBlackListXML = new File("ipBlackList.xml");
    private static File serverConfXML = new File ("serverConf.xml");

    private static SortedSet<Player> allPlayersSet = new TreeSet<>(Comparator.comparing(Player::getLogin));
    private static SortedSet<Player> onlinePlayersSet = new TreeSet<>(Comparator.comparing(Player::getLogin));
    private static SortedSet<Player> ingamePlayersSet = new TreeSet<>(Comparator.comparing(Player::getLogin));
    private static HashSet<String> ipBlackListSet = new HashSet<>();
    private static HashSet<PlayerController> allPlayersControllerSet = new HashSet<>();

    private static final int PORT = 9001;
    private static int countOfThread = 0;


    public static int getCountOfThread() {
        return countOfThread;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("THE SERVER IS RUNNING");
        ServerSocket listener = new ServerSocket(PORT);
        checkServerXMLfiles();
        updatePlayersSets();
        updateIpBlackListSet();

        try {
            while (true) {
                PlayerController pc;
                pc = new PlayerController();
                //new PlayerController(listener.accept()).start();
                pc.setSocket(listener.accept());
                pc.start();
                allPlayersControllerSet.add(pc);
                countOfThread++;
                System.out.println("\n/ thread #" + countOfThread + " now is listening /");
            }
        } finally {
            listener.close();
        }
    }

    public static void checkServerXMLfiles() throws XMLStreamException, JAXBException {
        //проверка наличия playerList.xml и создание при негативном результате
        if (!playerListXML.exists()) {

            PlayerList playerList = new PlayerList();

            playerList.setPlayerList(new ArrayList<Player>());
            //создаём два игрока
            Player p1 = new Player();
            p1.setLogin("admin");
            p1.setPassword("admin");
            p1.setStatus("offline");
            p1.setRank(100);

            Player p2 = new Player();
            p2.setLogin("hacker");
            p2.setPassword("lol");
            p2.setStatus("offline");
            p2.setRank(9999);

            //добавляем в список
            playerList.getPlayerList().add(p1);
            playerList.getPlayerList().add(p2);

            JAXBContext jaxbContext = JAXBContext.newInstance(PlayerList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //Marsha-им плеерлист в консоль
            jaxbMarshaller.marshal(playerList, System.out);

            //Marshal-им плеерлист в файл
            jaxbMarshaller.marshal(playerList, new File(playerListXML.getPath()));
        }

        //проверка наличия ipBlackList.xml и создание при негативном результате
        if (!ipBlackListXML.exists()) {

            IpBlackList ipBlackList = new IpBlackList();

            ipBlackList.setIpBlackList(new ArrayList<String>());
            //создаём два айпи

            ipBlackList.getIpBlackList().add("250.250.250.251");
            ipBlackList.getIpBlackList().add("250.250.250.252");

            JAXBContext jaxbContext = JAXBContext.newInstance(IpBlackList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //Marsha-им лист в консоль
            jaxbMarshaller.marshal(ipBlackList, System.out);

            //Marshal-им лист в файл
            jaxbMarshaller.marshal(ipBlackList, new File(ipBlackListXML.getPath()));
        }

        //проверка наличия serverConf.xml и создание при негативном результате
        if (!serverConfXML.exists()) {

            ServerConf serverConf = new ServerConf();

            serverConf.setHost("localhost");
            serverConf.setPort(getPORT());

            try {

                File file = new File(serverConfXML.getPath());
                JAXBContext jaxbContext = JAXBContext.newInstance(ServerConf.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                // output pretty printed
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                jaxbMarshaller.marshal(serverConf, file);
                jaxbMarshaller.marshal(serverConf, System.out);

            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updatePlayersSets(){
        updateAllPlayersSet();
        updateOnlinePlayersSet();
        updateIngamePlayersSet();
    }

    public static void updateAllPlayersSet(){
        try {
            File file = new File(playerListXML.getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(PlayerList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PlayerList playerList = (PlayerList) jaxbUnmarshaller.unmarshal(file);
            System.out.println("\nfound " + playerList.getPlayerList().size() + " players in playerList.xml:");
            allPlayersSet.addAll(playerList.getPlayerList());
            for (Player player : allPlayersSet){
                System.out.print(player.getLogin() + ", ");
            }
            System.out.println("\nupdated: allPlayersSet");
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static void updateOnlinePlayersSet(){
        for (Player player : allPlayersSet){
            if (player.getStatus().equals("online")){
                onlinePlayersSet.add(player);
            }
        }
        System.out.println("updated: onlinePlayersSet");
    }

    public static void updateIngamePlayersSet(){
        for (Player player : allPlayersSet){
            if (player.getStatus().equals("ingame")){
                ingamePlayersSet.add(player);
            }
        }
        System.out.println("updated: ingamePlayersSet");
    }

    public static void updateIpBlackListSet(){
        try {
            File file = new File(ipBlackListXML.getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(IpBlackList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            IpBlackList ipBlackList = (IpBlackList) jaxbUnmarshaller.unmarshal(file);
            System.out.println("\nfound " + ipBlackList.getIpBlackList().size() + " ip's in ipBlackList.xml:");
            for (int i = 0; i < ipBlackList.getIpBlackList().size(); i++){
                ipBlackListSet.add(ipBlackList.getIpBlackList().get(i));
                System.out.print(ipBlackList.getIpBlackList().get(i) + ", ");
            }
            System.out.println("\nupdated: ipBlackListSet");
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static Set<PlayerController> getAllPlayersControllerSet() {
        return allPlayersControllerSet;
    }

    public static int getPORT() {
        return PORT;
    }

    public static File getPlayerListXML() {
        return playerListXML;
    }

    public static void setPlayerListXML(File playerListXML) {
        Server.playerListXML = playerListXML;
    }

    public static File getIpBlackListXML() {
        return ipBlackListXML;
    }

    public static void setIpBlackListXML(File ipBlackListXML) {
        Server.ipBlackListXML = ipBlackListXML;
    }

    public static File getServerConfXML() {
        return serverConfXML;
    }

    public static void setServerConfXML(File serverConfXML) {
        Server.serverConfXML = serverConfXML;
    }

    public static Set<Player> getAllPlayersSet() {
        return allPlayersSet;
    }

    public static void setAllPlayersSet(SortedSet<Player> allPlayersSet) {
        Server.allPlayersSet = allPlayersSet;
    }

    public static SortedSet<Player> getOnlinePlayersSet() {
        return onlinePlayersSet;
    }

    public static void setOnlinePlayersSet(SortedSet<Player> onlinePlayersSet) {
        Server.onlinePlayersSet = onlinePlayersSet;
    }

    public static SortedSet<Player> getIngamePlayersSet() {
        return ingamePlayersSet;
    }

    public static void setIngamePlayersSet(SortedSet<Player> ingamePlayersSet) {
        Server.ingamePlayersSet = ingamePlayersSet;
    }

    public static HashSet<String> getIpBlackListSet() {
        return ipBlackListSet;
    }

    public static void setIpBlackListSet(HashSet<String> ipBlackListSet) {
        Server.ipBlackListSet = ipBlackListSet;
    }
}

@XmlRootElement(name = "ipBlackList")
@XmlAccessorType(XmlAccessType.FIELD)
class IpBlackList
{
    @XmlElement(name = "ip")
    private List<String> ipBlackList = null;

    public List<String> getIpBlackList() {
        return ipBlackList;
    }

    public void setIpBlackList(List<String> ipBlackList) {
        this.ipBlackList = ipBlackList;
    }
}


@XmlRootElement(name = "serverConf")
@XmlAccessorType(XmlAccessType.FIELD)
class ServerConf{

    String host;
    int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
