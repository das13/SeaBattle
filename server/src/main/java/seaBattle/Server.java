package seaBattle;

import org.apache.log4j.Logger;
import seaBattle.controller.PlayerController;
import seaBattle.model.Player;
import seaBattle.model.Status;
import seaBattle.services.xmlService.SaveLoadServerXML;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.*;

public class Server {

    private static File playerListXML = new File("playerList.xml");
    private static File bannedIpListXML = new File("bannedIpList.xml");
    private static File serverConfXML = new File("serverConf.xml");
    private static File adminsListXML = new File("adminsList.xml");

    private static SortedSet<Player> allPlayersSet = new TreeSet<>(Comparator.comparing(Player::getLogin));
    private static SortedSet<Player> onlinePlayersSet = new TreeSet<>(Comparator.comparing(Player::getLogin));
    private static SortedSet<Player> ingamePlayersSet = new TreeSet<>(Comparator.comparing(Player::getLogin));
    private static SortedSet<String> adminsSet = new TreeSet<>();
    private static SortedSet<String> bannedIpSet = new TreeSet<>();
    private static HashSet<PlayerController> allPlayersControllerSet = new HashSet<>();

    public final static Logger logger = Logger.getLogger(Server.class);

    private static int PORT;
    private static int countOfThread = 0;
    private static ServerSocket listener;

    /**
     * main method
     * @param args arguments
     */
    public static void main(String[] args) {
        serverLaunchPreparation();
        SaveLoadServerXML.readServerConfig();
        logger.info("THE SERVER IS RUNNING");
        startServer();
    }

    /**
     * method for start server
     */
    public static void startServer() {
        try {
            listener = new ServerSocket(PORT);

            try {
                while (true) {
                    PlayerController pc;
                    pc = new PlayerController();
                    pc.setSocket(listener.accept());
                    pc.start();
                    allPlayersControllerSet.add(pc);
                    countOfThread++;
                }
            } finally {
                listener.close();
            }
        } catch (BindException e) {
            logger.error("Error with starting server (PORT " + getPORT() + " is busy)", e);
        } catch (IOException e) {
            logger.error("Error with starting server", e);
        }
    }

    /**
     * load information from XML about all registered players
     * and filling sets
     */
    public static void serverLaunchPreparation() {
        SaveLoadServerXML.checkExistanceOrCreateServerXMLfiles();
        updateAllPlayersSet();
        updateOnlinePlayersSet();
        updateIngamePlayersSet();
        SaveLoadServerXML.updateAdminsSet();
        SaveLoadServerXML.updateBannedIpSet();
    }

    /**
     * update set of all registered players from playerList.xml
     */
    public static void updateAllPlayersSet() {
        SaveLoadServerXML.updateAllPlayersSet();
    }

    /**
     * update set of online players from allPlayerSet
     */
    public static void updateOnlinePlayersSet() {
        for (Player player : allPlayersSet) {
            if (Status.ONLINE.equals(player.getStatus())) {
                onlinePlayersSet.add(player);
            } else {
                onlinePlayersSet.remove(player);
            }
        }
    }

    /**
     * update set of ingame players from allPlayerSet
     */
    public static void updateIngamePlayersSet() {
        for (Player player : allPlayersSet) {
            if (Status.INGAME.equals(player.getStatus())) {
                ingamePlayersSet.add(player);
            } else {
                ingamePlayersSet.remove(player);
            }
        }
    }

    public static int getPORT() {
        return PORT;
    }

    public static void setPORT(int PORT) {
        Server.PORT = PORT;
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

    /**
     * rebooting server
     * @throws IOException
     */
    public static void reboot() throws IOException {
        logger.info("SERVER REBOOTING");
        listener.close();
        startServer();
    }

    /**
     * shutting down server
     */
    public static void shutdown() {
        logger.info("SERVER SHUTDOWN");
        System.exit(0);
    }
}