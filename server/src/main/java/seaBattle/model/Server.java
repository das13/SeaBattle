package seaBattle.model;

import org.apache.log4j.Logger;
import seaBattle.controller.PlayerController;
import seaBattle.model.serverFileService.AdminsList;
import seaBattle.model.serverFileService.BannedIpList;
import seaBattle.model.serverFileService.PlayerList;
import seaBattle.model.serverFileService.ServerConf;
import seaBattle.xmlservice.SaveLoadServerXML;

import javax.xml.bind.*;
import java.io.File;
import java.io.IOException;
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

    private final static Logger logger = Logger.getLogger(Server.class);

    private static int PORT;
    private static int countOfThread = 0;


    public static void main(String[] args) {
        serverLaunchPreparation();
        SaveLoadServerXML.readServerConfig();
        System.out.println("THE SERVER IS RUNNING");
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(PORT);


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
                System.out.println("#" + listener.getLocalPort() + " listener.getLocalPort()");
                System.out.println("#" + listener.getInetAddress() + " listener.getInetAddress()");
                System.out.println("#" + listener.getChannel() + " listener.getChannel()");
                System.out.println("#" + listener.getLocalSocketAddress() + " listener..getLocalSocketAddress()");
                System.out.println("#" + listener.getReuseAddress() + " listener.getReuseAddress()");
                      }
        } finally {
            listener.close();
        }
        } catch (IOException e) {
            logger.error("Error with starting server", e);
        }
    }

    public static void serverLaunchPreparation(){
        SaveLoadServerXML.checkServerXMLfiles();
        updateAllPlayersSet();
        updateOnlinePlayersSet();
        updateIngamePlayersSet();
        SaveLoadServerXML.updateAdminsSet();
        SaveLoadServerXML.updateBannedIpSet();
    }

    public static void updateAllPlayersSet(){
        SaveLoadServerXML.updateAllPlayersSet();
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
}