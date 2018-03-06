package seaBattle.xmlservice;

import org.apache.log4j.Logger;
import seaBattle.model.Player;
import seaBattle.model.Server;
import seaBattle.model.serverFileService.AdminsList;
import seaBattle.model.serverFileService.BannedIpList;
import seaBattle.model.serverFileService.PlayerList;
import seaBattle.model.serverFileService.ServerConf;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;

public class SaveLoadServerXML {

    private final static Logger logger = Logger.getLogger(SaveLoadServerXML.class);

    public static void readServerConfig(){
        try {
            File file = new File(Server.getServerConfXML().getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(ServerConf.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            ServerConf serverConf = (ServerConf) jaxbUnmarshaller.unmarshal(file);

            Server.setPORT(serverConf.getPort());
            //TODO добавить хост?
            //this.setHOST(serverConf.getPort());
            System.out.println("\nPort updated from serverConf.xml");
        } catch (JAXBException e) {
            logger.error("serverConf.xml reading error.", e);
        }
    }

    public static void checkServerXMLfiles() {
        //проверка наличия playerList.xml и создание при негативном результате
        if (!Server.getPlayerListXML().exists()) {

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

            JAXBContext jaxbContext = null;
            try {
                jaxbContext = JAXBContext.newInstance(PlayerList.class);
                Marshaller jaxbMarshaller = null;
                jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                //Marsha-им плеерлист в консоль
                jaxbMarshaller.marshal(playerList, System.out);
                //Marshal-им плеерлист в файл
                jaxbMarshaller.marshal(playerList, new File(Server.getPlayerListXML().getPath()));
            } catch (JAXBException e) {
                logger.error("playerList.xml creation error.", e);
            }
        }

        //проверка наличия bannedIpList.xml и создание при негативном результате
        if (!Server.getBannedIpListXML().exists()) {

            BannedIpList bannedIpList = new BannedIpList();

            bannedIpList.setBannedIpList(new ArrayList<String>());
            //создаём два айпи

            bannedIpList.getBannedIpList().add("250.250.250.251");
            bannedIpList.getBannedIpList().add("250.250.250.252");
            try{
                JAXBContext jaxbContext = JAXBContext.newInstance(BannedIpList.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                //Marsha-им лист в консоль
                jaxbMarshaller.marshal(bannedIpList, System.out);

                //Marshal-им лист в файл
                jaxbMarshaller.marshal(bannedIpList, new File(Server.getBannedIpListXML().getPath()));
            } catch (JAXBException e) {
                logger.error("bannedIpList.xml creation error.", e);
            }
        }

        //проверка наличия adminsList.xml и создание при негативном результате
        if (!Server.getAdminsListXML().exists()) {

            AdminsList adminsList = new AdminsList();

            adminsList.setAdminList(new ArrayList<String>());

            //создаём два логина
            adminsList.getAdminList().add("admin");
            adminsList.getAdminList().add("hacker");
            try{
                JAXBContext jaxbContext = JAXBContext.newInstance(AdminsList.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                //Marsha-им лист в консоль
                jaxbMarshaller.marshal(adminsList, System.out);

                //Marshal-им лист в файл
                jaxbMarshaller.marshal(adminsList, new File(Server.getAdminsListXML().getPath()));
            } catch (JAXBException e) {
                logger.error("adminsList.xml creation error.", e);
            }
        }

        //проверка наличия serverConf.xml и создание при негативном результате
        if (!Server.getServerConfXML().exists()) {

            ServerConf serverConf = new ServerConf();

            serverConf.setHost("localhost");
            serverConf.setPort(9001);

            try {

                File file = new File(Server.getServerConfXML().getPath());
                JAXBContext jaxbContext = JAXBContext.newInstance(ServerConf.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                // output pretty printed
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                jaxbMarshaller.marshal(serverConf, file);
                jaxbMarshaller.marshal(serverConf, System.out);

            } catch (JAXBException e) {
                logger.error("serverConf.xml creation error.", e);
            }
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
            logger.error("Updating playerList.xml error.", e);
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
            logger.error("Updating adminsList.xml error.", e);
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
            logger.error("Updating bannedIpList.xml error.", e);
        }
    }

    public static void updateAdminsSet(){
        try {
            File file1 = new File(Server.getAdminsListXML().getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(AdminsList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            AdminsList adminsList = (AdminsList) jaxbUnmarshaller.unmarshal(file1);
            System.out.println("\nfound " + adminsList.getAdminList().size() + " admins in adminsList.xml:");
            for (int i = 0; i < adminsList.getAdminList().size(); i++){
                Server.getAdminsSet().add(adminsList.getAdminList().get(i));
                System.out.print(adminsList.getAdminList().get(i) + ", ");
            }
            System.out.println("\nupdated: adminsSet");
            updateAdminsListXML();
        } catch (JAXBException e) {
            logger.error("Updating adminsSet error.", e);
        }
    }

    public static void updateBannedIpSet(){
        try {
            File file = new File(Server.getBannedIpListXML().getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(BannedIpList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            BannedIpList bannedIpList = (BannedIpList) jaxbUnmarshaller.unmarshal(file);
            System.out.println("\nfound " + bannedIpList.getBannedIpList().size() + " ip's in bannedIpList.xml:");
            for (int i = 0; i < bannedIpList.getBannedIpList().size(); i++){
                Server.getBannedIpListSet().add(bannedIpList.getBannedIpList().get(i));
                System.out.print(bannedIpList.getBannedIpList().get(i) + ", ");
            }
            System.out.println("\nupdated: bannedIpSet");
            updateBannedIpListXML();
        } catch (JAXBException e) {
            logger.error("Updating bannedIpSet error.", e);
        }
    }
}
