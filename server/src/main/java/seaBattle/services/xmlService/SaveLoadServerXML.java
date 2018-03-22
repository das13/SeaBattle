package seaBattle.services.xmlService;

import org.apache.log4j.Logger;
import seaBattle.model.Field;
import seaBattle.model.Player;
import seaBattle.Server;
import seaBattle.model.Status;
import seaBattle.services.serverFileService.*;

import javax.xml.bind.*;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <code>SaveLoadServerXML</code> class
 * loads/saves info from/to local server XML files.
 * XML files restore info when server launching,
 * backup specific changes while the server is running.
 * @author Oleksandr Symonenko
 */

public class SaveLoadServerXML {

    private static final String PLAYERSLIST_XSD = "pl.xsd";
    private static final String BANNEDIPLIST_XSD = "bil.xsd";
    private static final String ADMINSLIST_XSD = "al.xsd";
    private static final String SERVERCONF_XSD = "sc.xsd";

    private final static Logger logger = Logger.getLogger(SaveLoadServerXML.class);

    /**
     * <code>readServerConfig</code> read info from serverConf.xml when server starts
     */
    public static void readServerConfig(){
        if(!Server.getPlayerListXML().exists()) {
            logger.warn("Reading XML file, but serverConf.xml not found. Creating... ");
            checkExistanceOrCreateServerXMLfiles();
        }
        try {
            File file = new File(Server.getServerConfXML().getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(ServerConf.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            ServerConf serverConf = (ServerConf) jaxbUnmarshaller.unmarshal(file);

            Server.setPORT(serverConf.getPort());
        } catch (JAXBException e) {
            logger.error("serverConf.xml reading error.", e);
        }
    }

    /**
     * <code>checkExistanceOrCreateServerXMLfiles</code> check existence
     * or create xml server files if some of them is not exist
     */
    public static void checkExistanceOrCreateServerXMLfiles() {
        if (!Server.getPlayerListXML().exists()) {
            createPlayerListXML();
        }
        if (!Server.getBannedIpListXML().exists()) {
            createBannedIpListXML();
        }
        if (!Server.getAdminsListXML().exists()) {
            createAdminsListXML();
        }
        if (!Server.getServerConfXML().exists()) {
            createServerConfXML();
        }
    }

    /**
     * the <code>createPlayerListXML</code> method
     * creates playerList.xml with basic info inside
     */
    public static void createPlayerListXML(){
        PlayerList playerList = new PlayerList();

        playerList.setPlayerList(new ArrayList<Player>());
        Player p1 = new Player();
        p1.setLogin("admin");
        p1.setPassword("admin");
        p1.setStatus(Status.OFFLINE);
        p1.setRank(100);

        Player p2 = new Player();
        p2.setLogin("hacker");
        p2.setPassword("lol");
        p2.setStatus(Status.OFFLINE);
        p2.setRank(9999);

        playerList.getPlayerList().add(p1);
        playerList.getPlayerList().add(p2);

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(PlayerList.class);
            Marshaller jaxbMarshaller = null;
            jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(playerList, System.out);
            jaxbMarshaller.marshal(playerList, new File(Server.getPlayerListXML().getPath()));
        } catch (JAXBException e) {
            logger.error("playerList.xml creation error.", e);
        }
    }

    /**
     * the <code>createBannedIpListXML</code> method
     * creates bannedIpList.xml with basic info inside
     */
    public static void createBannedIpListXML(){
        BannedIpList bannedIpList = new BannedIpList();

        bannedIpList.setBannedIpList(new ArrayList<String>());

        bannedIpList.getBannedIpList().add("250.250.250.251");
        bannedIpList.getBannedIpList().add("250.250.250.252");
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(BannedIpList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(bannedIpList, System.out);

            jaxbMarshaller.marshal(bannedIpList, new File(Server.getBannedIpListXML().getPath()));
        } catch (JAXBException e) {
            logger.error("bannedIpList.xml creation error.", e);
        }
    }

    /**
     * the <code>createAdminsListXML</code> method
     * creates adminsList.xml with basic info inside
     */
    public static void createAdminsListXML(){
        AdminsList adminsList = new AdminsList();

        adminsList.setAdminList(new ArrayList<String>());

        adminsList.getAdminList().add("admin");
        adminsList.getAdminList().add("hacker");
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(AdminsList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(adminsList, System.out);

            jaxbMarshaller.marshal(adminsList, new File(Server.getAdminsListXML().getPath()));
        } catch (JAXBException e) {
            logger.error("adminsList.xml creation error.", e);
        }
    }

    /**
     * the <code>createServerConfXML</code> method
     * creates serverConf.xml with basic info inside
     */
    public static void createServerConfXML(){
        ServerConf serverConf = new ServerConf();

        serverConf.setHost("localhost");
        serverConf.setPort(9001);

        try {

            File file = new File(Server.getServerConfXML().getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(ServerConf.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(serverConf, file);
            jaxbMarshaller.marshal(serverConf, System.out);

        } catch (JAXBException e) {
            logger.error("serverConf.xml creation error.", e);
        }
    }

    /**
     * the <code>updatePlayerListXML</code> method
     * updates playerList.xml with info from allPlayersSet
     */
    public static void updatePlayerListXML(){
        if(!Server.getPlayerListXML().exists()) {
            logger.warn("Updating XML file, but playerList.xml not found. Creating... ");
            checkExistanceOrCreateServerXMLfiles();
        }
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
            jaxbMarshaller.marshal(playerList, new File(Server.getPlayerListXML().getPath()));
        } catch (JAXBException e) {
            logger.error("Updating playerList.xml error.", e);
        }
    }

    /**
     * the <code>updateAdminsListXML</code> method
     * updates adminsList.xml with info from adminsSet
     */
    public static void updateAdminsListXML(){
        if(!Server.getAdminsListXML().exists()) {
            logger.warn("Updating XML file, but adminsList.xml not found. Creating... ");
            checkExistanceOrCreateServerXMLfiles();
        }
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
            jaxbMarshaller.marshal(adminsList, new File(Server.getAdminsListXML().getPath()));
        } catch (JAXBException e) {
            logger.error("Updating adminsList.xml error.", e);
        }
    }

    /**
     * the <code>updateBannedIpListXML</code> method
     * updates bannedIpList.xml with info from bannedIpSet
     */
    public static void updateBannedIpListXML(){
        if(!Server.getBannedIpListXML().exists()) {
            logger.warn("Updating XML file, but bannedIpList.xml not found. Creating... ");
            checkExistanceOrCreateServerXMLfiles();
        }
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
            jaxbMarshaller.marshal(bannedIpList, new File(Server.getBannedIpListXML().getPath()));
        } catch (JAXBException e) {
            logger.error("Updating bannedIpList.xml error.", e);
        }
    }

    /**
     * the <code>updateAllPlayersSet</code> method
     * updates allPlayersSet with info from PlayerController class,
     * then called method <code>updatePlayerListXML</code> to backup changes
     */
    public static void updateAllPlayersSet() {
        if (!Server.getPlayerListXML().exists()) {
            logger.warn("Updating set, but file playerList.xml NOT FOUND. Creating... ");
            checkExistanceOrCreateServerXMLfiles();
        }
        if (!checkXMLsafety(Server.getPlayerListXML(),PLAYERSLIST_XSD, PlayerList.class)){
            logger.warn("Updating set, but file playerList.xml WAS DAMAGED. Repairing... ");
            createPlayerListXML();
            checkExistanceOrCreateServerXMLfiles();
        }
        try {
            File file = new File(Server.getPlayerListXML().getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(PlayerList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PlayerList playerList = (PlayerList) jaxbUnmarshaller.unmarshal(file);
            Server.getAllPlayersSet().addAll(playerList.getPlayerList());
            if (Server.getAllPlayersControllerSet().isEmpty()) {
                for (Player pl : Server.getAllPlayersSet()) {
                    if (!Status.OFFLINE.equals(pl.getStatus()) && !Status.BANNED.equals(pl.getStatus())) {
                        pl.setStatus(Status.OFFLINE);
                    }
                }
            }
            SaveLoadServerXML.updatePlayerListXML();

            } catch(JAXBException e){
            logger.error("Updating allPlayersSet error.", e);
        }
    }

    /**
     * the <code>updateAdminsSet</code> method
     * updates adminsSet with info from PlayerController class,
     * then called method <code>updateAdminsListXML</code> to backup changes
     */
    public static void updateAdminsSet(){
        if(!Server.getAdminsListXML().exists()) {
            logger.warn("Updating set, but file adminsList.xml not found. Creating... ");
            checkExistanceOrCreateServerXMLfiles();
        }
        if (!checkXMLsafety(Server.getAdminsListXML(),ADMINSLIST_XSD, AdminsList.class)){
            logger.warn("Updating set, but file adminsList.xml WAS DAMAGED. Repairing... ");
            createAdminsListXML();
            checkExistanceOrCreateServerXMLfiles();
        }
        try {
            File file1 = new File(Server.getAdminsListXML().getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(AdminsList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            AdminsList adminsList = (AdminsList) jaxbUnmarshaller.unmarshal(file1);
            for (int i = 0; i < adminsList.getAdminList().size(); i++){
                Server.getAdminsSet().add(adminsList.getAdminList().get(i));
            }
            updateAdminsListXML();
        } catch (JAXBException e) {
            logger.error("Updating adminsSet error.", e);
        }
    }

    /**
     * the <code>updateBannedIpSet</code> method
     * updates bannedIpSet with info from PlayerController class,
     * then called method <code>updateBannedIpListXML</code> to backup changes
     */
    public static void updateBannedIpSet(){
        if(!Server.getBannedIpListXML().exists()) {
            logger.warn("Updating set, but file bannedIpList.xml not found. Creating... ");
            checkExistanceOrCreateServerXMLfiles();
        }
        if (!checkXMLsafety(Server.getBannedIpListXML(),BANNEDIPLIST_XSD, BannedIpList.class)){
            logger.warn("Updating set, but file bannedIpList.xml WAS DAMAGED. Repairing... ");
            createBannedIpListXML();
            checkExistanceOrCreateServerXMLfiles();
        }
        try {
            File file = new File(Server.getBannedIpListXML().getPath());
            JAXBContext jaxbContext = JAXBContext.newInstance(BannedIpList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            BannedIpList bannedIpList = (BannedIpList) jaxbUnmarshaller.unmarshal(file);
            for (int i = 0; i < bannedIpList.getBannedIpList().size(); i++){
                Server.getBannedIpListSet().add(bannedIpList.getBannedIpList().get(i));
            }
            updateBannedIpListXML();
        } catch (JAXBException e) {
            logger.error("Updating bannedIpSet error.", e);
        }
    }

    /**
     * the <code>checkXMLsafety</code> method
     * checks specific XML file for damages
     */
    public static boolean checkXMLsafety(File XMLforCheck, String XMLschema, Class c) {
        File xmlDelOrNot = null;
        String sourceXML = XMLforCheck.getPath();
        JAXBContext context;
        PlayerList myplayerList;

        try {
            xmlDelOrNot = new File(sourceXML);
            if (xmlDelOrNot.exists()) {
                context = JAXBContext.newInstance(c);
                File xmlDelOrNot1 = new File(XMLschema);
                context.generateSchema(new SchemaOutputResolver() {
                    @Override
                    public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                        StreamResult result = new StreamResult(new FileOutputStream(xmlDelOrNot1));
                        result.setSystemId(xmlDelOrNot1.getAbsolutePath());
                        return result;
                    }
                });
                xmlDelOrNot1.delete();
                Unmarshaller unMarshaller = context.createUnmarshaller();
                Object xmlObject = c.cast(unMarshaller.unmarshal(xmlDelOrNot));
            } else {
                logger.error("checked XML " + XMLforCheck + " is not exist");
            }
        } catch (IOException e) {
            logger.error("IOE when checkXMLsafety", e);
            return false;
        } catch (JAXBException e) {
            logger.error("JAXBE when checkXMLsafety", e);
            return false;
        }
        return true;
    }

    /**
     * the <code>saveGame</code> method
     * saves game info from GameController class
     */
    public static void saveGame(String playerTurnTime, String player1, Field field1, String player2, Field field2){
        if (!new File("game-" + player1 +"VS"+ player2 + ".xml").exists()){

            GameCondition gameCondition = new GameCondition();

            gameCondition.setPlayerInGameList(new ArrayList<PlayerInGame>());

            PlayerInGame playerInGame1 = new PlayerInGame();
            PlayerInGame playerInGame2 = new PlayerInGame();

            playerInGame1.setLogin(player1);
            playerInGame2.setLogin(player2);

            playerInGame1.setTurnTime(playerTurnTime);
            playerInGame2.setTurnTime(playerTurnTime);

            String[][] tempList = new String[2][];
            tempList[0] = playerInGame1.getRow();
            tempList[1] = playerInGame2.getRow();

            StringBuilder sb = new StringBuilder();

            for (int t = 0; t < 2; t++) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (t == 0){
                            sb.append(String.valueOf(field1.getField()[i][j]));
                        }
                        else {
                            sb.append(String.valueOf(field2.getField()[i][j]));
                        }
                    }
                    tempList[t][i] = String.valueOf(sb);
                    sb.delete(0, 10);
                }
            }

            gameCondition.getPlayerInGameList().add(playerInGame1);
            gameCondition.getPlayerInGameList().add(playerInGame2);

            try {
                File file = new File(new File("game-" + player1 +"VS"+ player2 + ".xml").getPath());
                JAXBContext jaxbContext = JAXBContext.newInstance(GameCondition.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                jaxbMarshaller.marshal(gameCondition, file);
                jaxbMarshaller.marshal(gameCondition, System.out);

            } catch (JAXBException e) {
                logger.error("serverConf.xml creation error.", e);
            }
        }
    }
}
