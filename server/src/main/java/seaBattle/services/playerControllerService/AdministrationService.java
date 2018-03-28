package seaBattle.services.playerControllerService;

import org.apache.log4j.Logger;
import seaBattle.Server;
import seaBattle.controller.PlayerController;
import seaBattle.model.Player;
import seaBattle.model.Status;
import seaBattle.services.xmlService.SaveLoadServerXML;

import java.io.IOException;

public class AdministrationService {

    private PlayerController pcThis;
    private String str;
    private final Logger logger = Logger.getLogger(PlayerController.class);

    public AdministrationService(PlayerController pc1){
        pcThis = pc1;
    }

    /**
     * <code>banPlayerResult</code> returns result of Client try to ban chosen player
     * @param playerToBan - players login to ban
     * @return - result
     */
    public String banPlayerResult(String playerToBan) {
        for (String login : Server.getAdminsSet()) {
            if (pcThis.getThisPlayer().getLogin().equals(login)) {
                for (Player pl1 : Server.getAllPlayersSet()) {
                    if (pl1.getLogin().equals(playerToBan)) {
                        if (Status.BANNED.equals(pl1.getStatus())){
                            str = "player " + playerToBan + " already banned";
                            return str;
                        }
                        pl1.setStatus(Status.BANNED);
                        SaveLoadServerXML.updatePlayerListXML();
                        str = "player " + playerToBan + " was banned by " + pcThis.getThisPlayer().getLogin();
                        break;
                    }
                }
                try {
                    for (PlayerController pc1 : Server.getAllPlayersControllerSet()) {
                        if (pc1.getThisPlayer().getLogin().equals(playerToBan)) {
                            pc1.getSocket().close();
                            Server.getAllPlayersControllerSet().remove(pc1);
                            SaveLoadServerXML.updatePlayerListXML();
                            str = "player " + playerToBan + " was banned and kicked by " + pcThis.getThisPlayer().getLogin();
                            return str;
                        }
                    }
                } catch (IOException e) {
                    logger.error("Error with kicking player", e);
                }
            }
        }
        return str;
    }

    /**
     * <code>unbanPlayerResult</code> returns result of Client try to ban chosen player
     * @param playerToUnBan - players login to ban
     * @return - result
     */
    public String unbanPlayerResult(String playerToUnBan) {
        for (String login : Server.getAdminsSet()) {
            if (pcThis.getThisPlayer().getLogin().equals(login)) {
                for (Player pl1 : Server.getAllPlayersSet()) {
                    if (pl1.getLogin().equals(playerToUnBan)) {
                        if (!Status.BANNED.equals(pl1.getStatus())){
                            str = "player " + playerToUnBan + " already unbanned";
                            return str;
                        }
                        pl1.setStatus(Status.OFFLINE);
                        SaveLoadServerXML.updatePlayerListXML();
                        str = "player " + playerToUnBan + " was unbanned by " + pcThis.getThisPlayer().getLogin();
                        return str;
                    }
                }
            }
        }
        str = "player with login " + playerToUnBan + " was not found in playerList.";
        return str;
    }

    /**
     * <code>banIpResult</code> returns result of Client try to ban chosen ip
     * @param ipToBan - ip to ban
     * @return - result
     * @throws IOException
     */
    public String banIpResult(String ipToBan) throws IOException {
        for (String login : Server.getAdminsSet()) {
            if (pcThis.getThisPlayer().getLogin().equals(login)) {
                for (String ip1 : Server.getBannedIpListSet()) {
                    if (ip1.equals(ipToBan)) {
                        str = "ip " + ipToBan + " already banned";
                        break;
                    }
                }
                Server.getBannedIpListSet().add(ipToBan);
                str = "ip " + ipToBan + " banned by " + pcThis.getThisPlayer().getLogin();
                for (PlayerController pc : Server.getAllPlayersControllerSet()) {
                    for (String ip : Server.getBannedIpListSet()) {
                        if (pc.getSocket().getInetAddress().toString().equals(ip)) {
                            str = "ip " + ipToBan + " banned, and " + pc.getThisPlayer().getLogin() + " kicked by " + pcThis.getThisPlayer().getLogin();
                            pc.getSocket().close();
                        }
                    }
                }
                SaveLoadServerXML.updateBannedIpListXML();
            }
        }
        return str;
    }

    /**
     * <code>unbanIpResult</code> returns result of Client try to ban chosen ip
     * @param ipToUnBan - ip to ban
     * @return - result
     * @throws IOException
     */
    public String unbanIpResult(String ipToUnBan) throws IOException {
        for (String login : Server.getAdminsSet()) {
            if (pcThis.getThisPlayer().getLogin().equals(login)) {
                for (String ip1 : Server.getBannedIpListSet()) {
                    if (ip1.equals(ipToUnBan)) {
                        Server.getBannedIpListSet().remove(ipToUnBan);
                        SaveLoadServerXML.updateBannedIpListXML();
                        str = "ip " + ipToUnBan + " was unbanned by " + pcThis.getThisPlayer().getLogin();
                        return str;
                    }
                }
                str = "ip " + ipToUnBan + " was not found in bannedIpList.";
            }
        }
        return str;
    }

    /**
     * <code>rebootServer</code> reboots server
     * @param login - needed for check access
     */
    public void rebootServer(String login) throws IOException {
        for (String login1 : Server.getAdminsSet()) {
            if (login1.equals(login)) {
                Server.reboot();
            }
        }
    }

    /**
     * <code>shutdownServer</code> is shutting server down
     * @param login - needed for check access
     */
    public void shutdownServer(String login) {
        for (String login1 : Server.getAdminsSet()) {
            if (login1.equals(login)) {
                Server.shutdown();
            }
        }
    }
}
