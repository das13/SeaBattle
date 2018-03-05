package seaBattle.model;

import seaBattle.controller.PlayerController;
import seaBattle.model.serverFileService.AdminsList;
import seaBattle.model.serverFileService.BannedIpList;
import seaBattle.model.serverFileService.PlayerList;
import seaBattle.model.serverFileService.ServerConf;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.net.ServerSocket;
import java.util.*;

public class Server {

    private static File playerListXML = new File("playerList.xml");
    private static File bannedIpListXML = new File("bannedIpList.xml");
    private static File serverConfXML = new File ("serverConf.xml");
    private static File adminsListXML = new File ("adminsList.xml");

    private static SortedSet<Player> allPlayersSet = new TreeSet<>(Comparator.comparing(Player::getLogin));
    private static SortedSet<Player> onlinePlayersSet = new TreeSet<>(Comparator.comparing(Player::getLogin));
    private static SortedSet<Player> ingamePlayersSet = new TreeSet<>(Comparator.comparing(Player::getLogin));
    private static SortedSet<String> adminsSet = new TreeSet<>();
    private static SortedSet<String> bannedIpSet = new TreeSet<>();
    private static HashSet<PlayerController> allPlayersControllerSet = new HashSet<>();

    private static final int PORT = 9001;
    private static int countOfThread = 0;


    public static void main(String[] args) throws Exception {
        System.out.println("THE SERVER IS RUNNING");
        ServerSocket listener = new ServerSocket(PORT);
        serverLaunchPreparation();

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

    public static void serverLaunchPreparation(){
        try {
            checkServerXMLfiles();
            updateAllPlayersSet();
            updateOnlinePlayersSet();
            updateIngamePlayersSet();
            updateAdminsSet();
            updateBannedIpSet();
        } catch (XMLStreamException | JAXBException e) {
            e.printStackTrace();
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

        //проверка наличия bannedIpList.xml и создание при негативном результате
        if (!bannedIpListXML.exists()) {

            BannedIpList bannedIpList = new BannedIpList();

            bannedIpList.setBannedIpList(new ArrayList<String>());
            //создаём два айпи

            bannedIpList.getBannedIpList().add("250.250.250.251");
            bannedIpList.getBannedIpList().add("250.250.250.252");

            JAXBContext jaxbContext = JAXBContext.newInstance(BannedIpList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //Marsha-им лист в консоль
            jaxbMarshaller.marshal(bannedIpList, System.out);

            //Marshal-им лист в файл
            jaxbMarshaller.marshal(bannedIpList, new File(bannedIpListXML.getPath()));
        }

        //проверка наличия adminsList.xml и создание при негативном результате
        if (!adminsListXML.exists()) {

            AdminsList adminsList = new AdminsList();

            adminsList.setAdminList(new ArrayList<String>());

            //создаём два логина
            adminsList.getAdminList().add("admin");
            adminsList.getAdminList().add("hacker");

            JAXBContext jaxbContext = JAXBContext.newInstance(AdminsList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //Marsha-им лист в консоль
            jaxbMarshaller.marshal(adminsList, System.out);

            //Marshal-им лист в файл
            jaxbMarshaller.marshal(adminsList, new File(adminsListXML.getPath()));
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
            updatePlayerListXML();
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


    public static void updateAdminsSet(){
        try {
            File file1 = new File(adminsListXML.getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(AdminsList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            AdminsList adminsList = (AdminsList) jaxbUnmarshaller.unmarshal(file1);
            System.out.println("\nfound " + adminsList.getAdminList().size() + " admins in adminsList.xml:");
            for (int i = 0; i < adminsList.getAdminList().size(); i++){
                adminsSet.add(adminsList.getAdminList().get(i));
                System.out.print(adminsList.getAdminList().get(i) + ", ");
            }
            System.out.println("\nupdated: adminsSet");
            updateAdminsListXML();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static void updateBannedIpSet(){
        try {
            File file = new File(bannedIpListXML.getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(BannedIpList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            BannedIpList bannedIpList = (BannedIpList) jaxbUnmarshaller.unmarshal(file);
            System.out.println("\nfound " + bannedIpList.getBannedIpList().size() + " ip's in bannedIpList.xml:");
            for (int i = 0; i < bannedIpList.getBannedIpList().size(); i++){
                bannedIpSet.add(bannedIpList.getBannedIpList().get(i));
                System.out.print(bannedIpList.getBannedIpList().get(i) + ", ");
            }
            System.out.println("\nupdated: bannedIpSet");
            updateBannedIpListXML();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


    public static void updatePlayerListXML(){
        try {
            PlayerList playerList = new PlayerList();
            playerList.setPlayerList(new ArrayList<>());
            for (Player p1 : Server.getAllPlayersSet()) {
                playerList.getPlayerList().add(p1);
            }
            JAXBContext jaxbContext = null;
            jaxbContext = JAXBContext.newInstance(PlayerList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Marsha-им плеерлист в консоль
//            System.out.println("UPDATING PLAYERLIST XML:");
//            jaxbMarshaller.marshal(playerList, System.out);
            //Marshal-им плеерлист в файл
            jaxbMarshaller.marshal(playerList, new File(Server.getPlayerListXML().getPath()));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static void updateAdminsListXML(){
        try {
            AdminsList adminsList = new AdminsList();
            adminsList.setAdminList(new ArrayList<>());
            for (String login : Server.getAdminsSet()) {
                adminsList.getAdminList().add(login);
            }
            JAXBContext jaxbContext = null;
            jaxbContext = JAXBContext.newInstance(AdminsList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Marsha-им админлист в консоль
//            System.out.println("UPDATING ADMINSLIST XML:");
//            jaxbMarshaller.marshal(adminsList, System.out);
            //Marshal-им админлист в файл
            jaxbMarshaller.marshal(adminsList, new File(Server.getAdminsListXML().getPath()));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static void updateBannedIpListXML(){
        try {
            BannedIpList bannedIpList = new BannedIpList();
            bannedIpList.setBannedIpList(new ArrayList<>());
            for (String ip : Server.getBannedIpListSet()) {
                bannedIpList.getBannedIpList().add(ip);
            }
            JAXBContext jaxbContext = null;
            jaxbContext = JAXBContext.newInstance(BannedIpList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Marsha-им айпибанлист в консоль
//            System.out.println("UPDATING BANNEDIPLIST XML:");
//            jaxbMarshaller.marshal(bannedIpList, System.out);
            //Marshal-им айпибанлист в файл
            jaxbMarshaller.marshal(bannedIpList, new File(Server.getBannedIpListXML().getPath()));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


    //gettes and setters

    public static int getPORT() {
        return PORT;
    }

    public static int getCountOfThread() {
        return countOfThread;
    }
    public static void setCountOfThread(int countOfThread) {
        Server.countOfThread = countOfThread;
    }

    public static File getPlayerListXML() {
        return playerListXML;
    }
    public static void setPlayerListXML(File playerListXML) {
        Server.playerListXML = playerListXML;
    }

    public static File getBannedIpListXML() {
        return bannedIpListXML;
    }
    public static void setBannedIpListXML(File bannedIpListXML) {
        Server.bannedIpListXML = bannedIpListXML;
    }

    public static File getServerConfXML() {
        return serverConfXML;
    }
    public static void setServerConfXML(File serverConfXML) {
        Server.serverConfXML = serverConfXML;
    }

    public static File getAdminsListXML() {
        return adminsListXML;
    }
    public static void setAdminsListXML(File adminsListXML) {
        Server.adminsListXML = adminsListXML;
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

    public static SortedSet<String> getAdminsSet() {
        return adminsSet;
    }
    public static void setAdminsSet(SortedSet<String> adminsSet) {
        Server.adminsSet = adminsSet;
    }

    public static SortedSet<String> getBannedIpListSet() {
        return bannedIpSet;
    }
    public static void setBannedIpListSet(SortedSet<String> bannedIpSet) {
        Server.bannedIpSet = bannedIpSet;
    }

    public static HashSet<PlayerController> getAllPlayersControllerSet() {
        return allPlayersControllerSet;
    }
    public static void setAllPlayersControllerSet(HashSet<PlayerController> allPlayersControllerSet) {
        Server.allPlayersControllerSet = allPlayersControllerSet;
    }
}

