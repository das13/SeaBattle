package seaBattle.services.playerControllerService;

import seaBattle.Server;
import seaBattle.controller.PlayerController;
import seaBattle.model.Player;

public class ServerService {

    private PlayerController pcThis;

    public ServerService(PlayerController pc1){
        pcThis = pc1;
    }

    /**
     * <code>updateAndSendPlayersInfo</code> updates XML files and send their Sets copy
     * to all aviable Clients, that choosing by existence of their PlayerControllers
     */
    public static void updateAndSendPlayersInfo(){
        Server.updateAllPlayersSet();
        Server.updateOnlinePlayersSet();
        Server.updateIngamePlayersSet();
        for (PlayerController pc : Server.getAllPlayersControllerSet()){
            if (!pc.getSocket().isClosed()){
                sendOnlinePlayers(pc);
                sendIngamePlayers(pc);
            }
        }
    }

    /**
     * <code>sendOnlinePlayers</code> uses by <code>updateAndSendPlayersInfo</code>
     * @param pc1 - receiver
     */
    public static void sendOnlinePlayers(PlayerController pc1) {
        String[] list = new String[((Server.getOnlinePlayersSet().size())*2)+1];
        String st = String.valueOf(Server.getOnlinePlayersSet().size());
        list[0] = st;
        int i = 1;
        for (Player player : Server.getOnlinePlayersSet()) {
            list[i++] = player.getLogin();
            list[i++] = String.valueOf(player.getRank());
        }
        pc1.getOutServerXML().send("ONLINE PLAYERS", list);
    }

    /**
     * <code>sendIngamePlayers</code> uses by <code>updateAndSendPlayersInfo</code>
     * @param pc1 - receiver
     */
    public static void sendIngamePlayers(PlayerController pc1) {
        String[] list = new String[Server.getIngamePlayersSet().size()+1];
        String st = String.valueOf(Server.getIngamePlayersSet().size());
        list[0] = st;
        int i = 1;
        for (Player player : Server.getIngamePlayersSet()) {
            list[i++] = player.getLogin();
        }
        pc1.getOutServerXML().send("INGAME PLAYERS", list);
    }

    /**
     * <code>msgServer</code> writes message to all Clients from server
     * @param msg - message
     */
    public void msgServer(String msg) {
        for (PlayerController pc : Server.getAllPlayersControllerSet()){
            pc.getOutServerXML().send("MSG", "SERVER: " + msg);
        }
    }

    /**
     * <code>msgResult</code> writes message to all Clients
     * @param msg - message
     */
    public void msgResult(String msg) {
        for (PlayerController pc : Server.getAllPlayersControllerSet()){
            pc.getOutServerXML().send("MSG", pcThis.getThisPlayer().getLogin() + ": " + msg);
        }
    }
}
