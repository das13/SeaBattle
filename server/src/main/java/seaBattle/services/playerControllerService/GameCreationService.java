package seaBattle.services.playerControllerService;

import seaBattle.Server;
import seaBattle.controller.GameController;
import seaBattle.controller.PlayerController;
import seaBattle.model.Player;
import seaBattle.model.Status;

public class GameCreationService {

    private PlayerController pcThis;
    private String str;

    public GameCreationService(PlayerController pc1){
        pcThis = pc1;
    }

    /**
     * <code>inviteResult</code> returns result of Client#1 invite player for play game
     * @param player1 - sender login
     * @param player2 - receiver login
     */
    public void inviteResult(String player1, String player2) {
        for (PlayerController pl: Server.getAllPlayersControllerSet()) {
            if (pl.getThisPlayer().getLogin().equals(player2)) {
                pl.getOutServerXML().send("INVITE",player1);
            }
        }
        pcThis.getOutServerXML().send("INFO","invite send to player: " + player2);
    }

    /**
     * <code>replyResult</code> returns result of Client#2 reply on invite for play game
     * @param player1 - receiver login
     * @param value - answer
     * @return - result
     */
    public String replyResult(String player1, String value) {

        for (PlayerController pc : Server.getAllPlayersControllerSet()) {
            if (pc.getThisPlayer().getLogin().equals(player1)) {
                if (pc.isWaitingForReply()) {
                    GameController gc = new GameController(pc, pcThis);
                    pcThis.setGc(gc);
                    pc.setGc(gc);
                    for (Player player : Server.getAllPlayersSet()){
                        if (player.getLogin().equals(pc.getThisPlayer().getLogin())){
                            player.setStatus(Status.INGAME);
                        }
                        if (player.getLogin().equals(pcThis.getThisPlayer().getLogin())){
                            player.setStatus(Status.INGAME);
                        }
                    }
                    pc.getOutServerXML().send("START GAME", pcThis.getThisPlayer().getLogin());
                    pcThis.getOutServerXML().send("START GAME", player1);
                    str = "OK";
                    break;
                }
                str = "ERROR! dude " + pc.getThisPlayer().getLogin() + " don't wanna play!";
            }
        }
        return str;
    }
}
