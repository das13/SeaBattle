package seaBattle.model;

import seaBattle.controller.PlayerController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.net.ServerSocket;
import java.util.*;

public class Server {
    private static final int PORT = 9001;

    private static Set<Player> allPlayersSet = Collections.synchronizedSet(new HashSet<Player>());
    private static Set<PlayerController> allPlayersControllerSet = Collections.synchronizedSet(new HashSet<PlayerController>());
    private static HashSet<Player> onlinePlayersSet = new HashSet<Player>();
    private static HashSet<Player> ingamePlayersSet = new HashSet<Player>();


    private static int countOfThread = 0;
    private static File playerListXML = new File("playerList.xml");
    private static File ipBlackListXML = new File("ipBlackList.xml");
    private static File serverConfXML = new File ("serverConf.xml");

    public static int getCountOfThread() {
        return countOfThread;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("THE SERVER IS RUNNING");
        ServerSocket listener = new ServerSocket(PORT);
        checkServerXMLfiles();
        loadAllPlayersSet();
        updateOnlinePlayersSet();
        updateIngamePlayersSet();

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

            IpBlackList ip1 = new IpBlackList();
            ip1.setIpAdress("250.250.250.250");

            try {

                File file = new File(ipBlackListXML.getPath());
                JAXBContext jaxbContext = JAXBContext.newInstance(IpBlackList.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                // output pretty printed
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                jaxbMarshaller.marshal(ip1, file);
                jaxbMarshaller.marshal(ip1, System.out);

            } catch (JAXBException e) {
                e.printStackTrace();
            }

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

    public static void loadAllPlayersSet(){
        try {
            File file = new File(playerListXML.getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(PlayerList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PlayerList playerList = (PlayerList) jaxbUnmarshaller.unmarshal(file);
            System.out.println("\nfound " + playerList.getPlayerList().size() + " players in playerList.xml:");
            for (int i = 0; i < playerList.getPlayerList().size(); i++) {
                System.out.print(playerList.getPlayerList().get(i).getLogin() + ", ");
                allPlayersSet.add(playerList.getPlayerList().get(i));
            }
            System.out.println();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static void updateAllPlayersSet(){
        try {
            File file = new File(playerListXML.getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(PlayerList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PlayerList playerList = (PlayerList) jaxbUnmarshaller.unmarshal(file);

            Set<Player> tempSet = allPlayersSet;
            allPlayersSet.removeAll(tempSet);

            allPlayersSet.addAll(playerList.getPlayerList());
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
    }

    public static void updateIngamePlayersSet(){
        for (Player player : allPlayersSet){
            if (player.getStatus().equals("ingame")){
                ingamePlayersSet.add(player);
            }
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

    public static void setAllPlayersSet(Set<Player> allPlayersSet) {
        Server.allPlayersSet = allPlayersSet;
    }

    public static HashSet<Player> getOnlinePlayersSet() {
        return onlinePlayersSet;
    }

    public static void setOnlinePlayersSet(HashSet<Player> onlinePlayersSet) {
        Server.onlinePlayersSet = onlinePlayersSet;
    }

    public static HashSet<Player> getIngamePlayersSet() {
        return ingamePlayersSet;
    }

    public static void setIngamePlayersSet(HashSet<Player> ingamePlayersSet) {
        Server.ingamePlayersSet = ingamePlayersSet;
    }
}


@XmlRootElement(name = "ipBlackList")
@XmlAccessorType(XmlAccessType.FIELD)
class IpBlackList{

    String ip;

    public String getIpAdress() {
        return ip;
    }


    public void setIpAdress(String ipAdress) {
        this.ip = ipAdress;
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
