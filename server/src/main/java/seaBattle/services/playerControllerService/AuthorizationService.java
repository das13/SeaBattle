package seaBattle.services.playerControllerService;

import seaBattle.Server;
import seaBattle.controller.PlayerController;
import seaBattle.model.Player;
import seaBattle.model.Status;

public class AuthorizationService {

    private PlayerController pcThis;
    private String str;

    public AuthorizationService(PlayerController pc1){
        pcThis = pc1;
    }

    /**
     * <code>authResult</code> returns result of Client authorization
     * @param login - login
     * @param password - password
     * @return - result
     */
    public String authResult(String login, String password) {
        for (Player player : Server.getAllPlayersSet()){
            if (!player.getLogin().equals(login) && !player.getPassword().equals(password)){
                str = "player with this login or password not found. register first";
            }
            if (player.getLogin().equals(login)){
                if (!player.getPassword().equals(password)){
                    str = "password is incorrect";
                }
                if (player.getPassword().equals(password) && Status.ONLINE.equals(player.getStatus())){
                    str = "player with nickname \"" + login + "\" already logged in";
                    return str;
                }
                if (player.getPassword().equals(password)){
                    for (String admin : Server.getAdminsSet()){
                        if (player.getLogin().equals(admin)){
                            str = "success! admin access.";
                            return str;
                        }
                    }
                    if (Status.BANNED.equals(player.getStatus())){
                        str = "MORTAL, YOUR ACCOUNT IS BANNED BY HIGHER POWER!";
                        return str;
                    }
                    for (String ip : Server.getBannedIpListSet()){
                        System.out.println(pcThis.toString());
                        System.out.println(pcThis.getSocket().toString());
                        System.out.println(pcThis.getSocket().getInetAddress().toString());
                        if (ip.equals(pcThis.getSocket().getInetAddress().toString())){
                            str = "MORTAL, YOUR IP IS BANNED BY HIGHER POWER!";
                            return str;
                        }
                    }
                    for (Player player1 : Server.getAllPlayersSet()){
                        if (player1.getLogin().equals(pcThis.getThisPlayer().getLogin())){
                            player1.setStatus(Status.ONLINE);
                            pcThis.getThisPlayer().setStatus(Status.ONLINE);
                            str = "success!";
                        }
                    }
                }
                return str;
            }
        }
        return str;
    }

    /**
     * <code>regResult</code> returns result of Client registration
     * @param login - login
     * @param password - password
     * @return - result
     */
    public String regResult(String login, String password){
        str = "";
        for (Player player : Server.getAllPlayersSet()){
            if (player.getLogin().equals(login)){
                str = "this login is already taken";
                break;
            }
        }
        if ("".equals(str)){
            Player player1 = new Player();
            player1.setLogin(login);
            player1.setPassword(password);
            player1.setStatus(Status.OFFLINE);
            player1.setRank(100);

            Server.getAllPlayersSet().add(player1);
            str = "success!";
        }
        return str;
    }

    /**
     * <code>logoutResult</code> returns result of logging out Client
     * @param login - players login
     * @return - result
     */
    public String logoutResult(String login) {
        for (Player player : Server.getAllPlayersSet()){
            if (player.getLogin().equals(login) && Status.ONLINE.equals(player.getStatus())){
                player.setStatus(Status.OFFLINE);
                str = "success!";
                Server.getAllPlayersControllerSet().remove(pcThis);
                return str;
            }
        }
        str = "error, you already logged out";
        return str;
    }
}
