package seaBattle.controller;

import org.apache.log4j.Logger;
import seaBattle.model.Player;
import seaBattle.model.Ship;
import seaBattle.model.Server;
import seaBattle.xmlservice.InServerXML;
import seaBattle.xmlservice.OutServerXML;
import seaBattle.xmlservice.SaveLoadServerXML;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.Socket;

public class PlayerController extends Thread {
    private Socket socket;
    private String str;
    private InServerXML inServerXML;
    private OutServerXML outServerXML;
    private Player thisPlayer;
    private GameController gc;
    private boolean waitingForReply;
    private final static Logger logger = Logger.getLogger(PlayerController.class);

    public PlayerController() {
        thisPlayer = new Player();
    }

    public PlayerController(Socket socket) throws IOException {
        thisPlayer = new Player();
        this.socket = socket;
        inServerXML = new InServerXML(socket);
        outServerXML = new OutServerXML(socket);
    }

    public void run() {
        int threadNumber = Server.getCountOfThread();
        thisPlayer.setStatus("offline");
        try {
            while (!socket.isClosed()){
                inServerXML.setReader(inServerXML.getFactory().createXMLStreamReader(inServerXML.getFileReader()));
                XMLStreamReader reader = inServerXML.getReader();
                while (reader.hasNext()) {
                    if (reader.getEventType() == 1 && reader.getLocalName().equals("key")){
                        reader.next();
                        switch (inServerXML.checkValue(reader)){
                            case "LOG IN": {
                                String login = inServerXML.checkValue(reader);
                                thisPlayer.setLogin(login);
                                System.out.println("\n\n\nkey \"LOG IN\" from " + this.getThisPlayer().getLogin() + " detected:");
                                System.out.println("login = \"" + login + "\"");
                                String password = inServerXML.checkValue(reader);
                                thisPlayer.setPassword(password);
                                System.out.println("password = \"" + password + "\"" + "\nSENDING ANSWER:");
                                outServerXML.send("INFO", authResult(login,password));
                                Server.updateAllPlayersSet();
                                Server.updateOnlinePlayersSet();
                                if (thisPlayer.getStatus().equals("online")) {
                                    sleep(10);
                                    sendOnlinePlayers();
                                    sleep(10);
                                    sendIngamePlayers();
                                }
                                break;
                            }
                            case "LOG OUT": {
                                System.out.println("\n\n\n\nkey \"LOG OUT\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String login = inServerXML.checkValue(reader);
                                outServerXML.send("INFO",logoutResult(login));
                                Server.updateAllPlayersSet();
                                Server.updateOnlinePlayersSet();
                                break;
                            }
                            case "REG": {
                                System.out.println("\n\n\n\nkey \"REG\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String login = inServerXML.checkValue(reader);
                                System.out.println("login = \"" + login + "\"");
                                String password = inServerXML.checkValue(reader);
                                System.out.println("password = \"" + password + "\"" + "\nSENDING ANSWER:");
                                outServerXML.send("INFO", regResult(login,password));
                                Server.updateAllPlayersSet();
                                break;
                            }
                            case "BAN PLAYER": {
                                System.out.println("\n\n\n\nkey \"BAN PLAYER\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String admin = inServerXML.checkValue(reader);
                                System.out.println("admin = \"" + admin + "\"");
                                String playerToBan = inServerXML.checkValue(reader);
                                System.out.println("player to ban = \"" + playerToBan + "\"" + "\nSENDING ANSWER:");
                                outServerXML.send("INFO", banPlayerResult(admin,playerToBan));
                                Server.updateAllPlayersSet();
                                Server.updateOnlinePlayersSet();
                                Server.updateIngamePlayersSet();
                                break;
                            }
                            case "BAN IP": {
                                System.out.println("\n\n\n\nkey \"BAN IP\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String admin = inServerXML.checkValue(reader);
                                System.out.println("admin = \"" + admin + "\"");
                                String ipToBan = inServerXML.checkValue(reader);
                                System.out.println("ip to ban = \"" + ipToBan + "\"" + "\nSENDING ANSWER:");
                                outServerXML.send("INFO", banIpResult(admin,ipToBan));
                                SaveLoadServerXML.updateBannedIpSet();
                                break;
                            }
                            case "INVITE": {
                                waitingForReply = true;
                                System.out.println("\n\n\nkey \"INVITE\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player1 = inServerXML.checkValue(reader);
                                System.out.println("player1 = \"" + player1 + "\"");
                                String player2 = inServerXML.checkValue(reader);
                                System.out.println("player2 = \"" + player2 + "\"");
                                inviteResult(player1,player2);
                                break;
                            }
                            //можно получить ТОЛЬКО от player2 (от того, кого пригласили)
                            case "REPLY": {
                                System.out.println("\n\n\nkey \"REPLY\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player1 = inServerXML.checkValue(reader);
                                System.out.println("player1 = \"" + player1 + "\"");
                                String reply = inServerXML.checkValue(reader);
                                System.out.println("reply = \"" + reply + "\"");
                                replyResult(player1,reply);
                                break;
                            }
                            case "SHIP LOCATION": {
                                System.out.println("\n\n\nkey \"SHIP LOCATION\" from " + this.getThisPlayer().getLogin() + " detected:");
                                int x1 = Integer.parseInt(inServerXML.checkValue(reader));
                                System.out.println("x1 = \"" + x1 + "\"");
                                int y1 = Integer.parseInt(inServerXML.checkValue(reader));
                                System.out.println("y1 = \"" + y1 + "\"");
                                int x2 = Integer.parseInt(inServerXML.checkValue(reader));
                                System.out.println("x2 = \"" + x2 + "\"");
                                int y2 = Integer.parseInt(inServerXML.checkValue(reader));
                                System.out.println("y2 = \"" + y2 + "\"");
                                if(!gc.checkStart()) {
                                    getOutServerXML().send("SHIP LOCATION", gc.setShip(this, new Ship(new int[]{x1, y1, x2, y2})));
                                }else {
                                    getOutServerXML().send("SHIP LOCATION", gc.setShip(this, new Ship(new int[]{x1, y1, x2, y2})) + " START GAME");
                                }
                                break;
                            }
                            case "SHOOT": {
                                System.out.println("\n\n\nkey \"SHOOT\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player = inServerXML.checkValue(reader);
                                System.out.println("player = \"" + player + "\"");
                                String x1 = inServerXML.checkValue(reader);
                                System.out.println("x1 = \"" + x1 + "\"");
                                String y1 = inServerXML.checkValue(reader);
                                System.out.println("y1 = \"" + y1 + "\"");
                                String x2 = inServerXML.checkValue(reader);
                                System.out.println("x2 = \"" + x2 + "\"");
                                String y2 = inServerXML.checkValue(reader);
                                System.out.println("y2 = \"" + y2 + "\"");
                                shootResult(player, x1,y1,x2,y2);
                                break;
                            }
                            case "SURRENDER": {
                                System.out.println("\n\n\nkey \"SURRENDER\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player = inServerXML.checkValue(reader);
                                System.out.println("player = \"" + player + "\"");
                                surrenderResult(player);
                                break;
                            }
                            case "GAME OVER": {
                                //это сервер должен определить когда отсылать гейм овер
                                System.out.println("xml message with key \"GAME OVER\" detected");
                            }
                            case "MSG": {
                                System.out.println("\n\n\nkey \"MSG\" from " + this.getThisPlayer().getLogin() + " detected:");
                                String player = inServerXML.checkValue(reader);
                                System.out.println("player = \"" + player + "\"");
                                String msg = inServerXML.checkValue(reader);
                                System.out.println("msg = \"" + msg + "\"");
                                msgResult(player,msg);
                                break;
                            }
                            default:{
                                System.out.println("UNKNOWN KEY RECEIVED FROM " + this.getThisPlayer().getLogin());
                                str = "unknown key";
                                getOutServerXML().send("INFO", str);
                            }
                        }
                    }
                    if (reader.isEndElement() && "root".equals(reader.getName().toString())) {
                        break;
                    }else{
                        reader.next();
                    }
                }
                reader.close();
            }
        } catch (XMLStreamException | InterruptedException e) {
            logger.error("Receive/Send error between thread and client", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Error with clothing thread", e);
            }
        }
    }

    public String authResult(String login, String password) {
        for (Player player : Server.getAllPlayersSet()){
            if (!player.getLogin().equals(login) && !player.getPassword().equals(password)){
                str = "player with this login or password not found. register first";
            }
            if (!player.getLogin().equals(login) && player.getPassword().equals(password) || player.getLogin().equals(login) && !player.getPassword().equals(password)){
                str = "login or password is incorrect";
                System.out.println("RESULT = " + str);
                return str;
            }
            if (player.getLogin().equals(login) && player.getPassword().equals(password) && player.getStatus().equals("online")){
                str = "player with nickname \"" + login + "\" already logged in";
                System.out.println("RESULT = " + str);
                return str;
            }
            if (player.getLogin().equals(login) && player.getPassword().equals(password)){
                if (player.getStatus().equals("banned")){
                    str = "MORTAL, YOUR ACCOUNT IS BANNED BY HIGHER POWER!";
                    break;
                }
                for (String ip : Server.getBannedIpListSet()){
                    if (socket.getInetAddress().equals(ip)){
                        str = "MORTAL, YOUR IP IS BANNED BY HIGHER POWER!";
                        break;
                    }
                }
                for (Player player1 : Server.getAllPlayersSet()){
                    if (player1.getLogin().equals(thisPlayer.getLogin())){
                        player1.setStatus("online");
                        thisPlayer.setStatus("online");
                        str = "success!";
                        System.out.println("RESULT = " + str);
                    }
                }
                return str;
            }
        }
        System.out.println("RESULT = " + str);
        return str;
    }

    public String regResult(String login, String password){
        str = "";
        for (Player player : Server.getAllPlayersSet()){
            if (player.getLogin().equals(login)){
                str = "this login is already taken";
                break;
            }
        }
        if (str.equals("")){
            Player player1 = new Player();
            player1.setLogin(login);
            player1.setPassword(password);
            player1.setStatus("offline");
            player1.setRank(100);

            Server.getAllPlayersSet().add(player1);
            str = "success!";
        }
        return str;
    }

    public String banPlayerResult(String admin,String playerToBan) {
        str = "something wrong";
        for (String login : Server.getAdminsSet()) {
            System.out.println("1. проверка в adminsSet. сравнивается " + admin + " и " + login);
            if (admin.equals(login)) {
                System.out.println("СОВПАЛО!");
                for (Player pl1 : Server.getAllPlayersSet()) {
                    System.out.println("2. поиск игрока в allPlayersSet. сравниваем " + playerToBan + " и " + pl1.getLogin());
                    if (pl1.getLogin().equals(playerToBan)) {
                        System.out.println("НАШЛИ! баним");
                        pl1.setStatus("banned");
                        str = "player " + playerToBan + " banned";
                        System.out.println("нужно ли кикать? смотрим");
                        break;
                    }
                }
                try {
                    System.out.println("размер сета контроллеров - " + Server.getAllPlayersControllerSet().size());
                    for (PlayerController pc1 : Server.getAllPlayersControllerSet()) {
                        System.out.println("3. поиск контроллера игрока. смотрим на контроллер игрока - " + pc1.getThisPlayer().getLogin());
                        if (pc1.getThisPlayer().getLogin().equals(playerToBan)) {
                            System.out.println("НАШЛИ! кикаем");
                            pc1.socket.close();
                            str = "player " + playerToBan + " banned and kicked";
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

    public String banIpResult(String admin,String ipToBan) {
        for (String login : Server.getAdminsSet()) {
            if (admin.equals(login)) {
                for (String ip1 : Server.getBannedIpListSet()) {
                    if (ip1.equals(ipToBan)) {
                        str = "ip " + ipToBan + " already banned";
                        break;
                    }
                }
                Server.getBannedIpListSet().add(ipToBan);
            }
        }
        return str;
    }

    //действие на приглашение игроком№1 игрока№2 в игру
    private void inviteResult(String player1, String player2) {
        for (PlayerController pl:Server.getAllPlayersControllerSet()) {
            if (pl.getThisPlayer().getLogin().equals(player2)) {
                pl.getOutServerXML().send("INVITE",player1);
            }
        }
        outServerXML.send("INFO","invite send to player: " + player2);
    }

    //действие на ответ игрока№2 игроку№1 на приглашение в игру
    private void replyResult(String player1, String value) {

        for (PlayerController pc : Server.getAllPlayersControllerSet()) {
            if (pc.getThisPlayer().getLogin().equals(player1)) {
                if (pc.isWaitingForReply()) {
                    gc = new GameController(pc, this);
                    pc.setGc(gc);
                    pc.getOutServerXML().send("START GAME", thisPlayer.getLogin());
                    System.out.print("\n");
                    outServerXML.send("START GAME", player1);
                    break;
                }
            }
            System.out.println("ERROR! dude " + pc + " don't wanna play!");
        }
    }

    public void sendOnlinePlayers() {
        if (thisPlayer.getStatus().equals("online")) {
            String[] list = new String[Server.getOnlinePlayersSet().size()+1];
            str = String.valueOf(Server.getOnlinePlayersSet().size());
            list[0] = str;
            int i = 1;
            System.out.println("\nОТПРАВЛЯЕМ ONLINE ИГРОКОВ");
            System.out.println("длина сета - " + Server.getOnlinePlayersSet().size());
            System.out.println("длина массива - " + list.length);
            for (Player player : Server.getOnlinePlayersSet()) {
                System.out.println("добавляем в ячейку массива №" + i + " - " + player.getLogin());
                list[i++] = player.getLogin();
            }
            outServerXML.send("ONLINE PLAYERS", list);
        }
    }

    public void sendIngamePlayers() {
        if (thisPlayer.getStatus().equals("online")) {
            String[] list = new String[Server.getIngamePlayersSet().size()+1];
            str = String.valueOf(Server.getIngamePlayersSet().size());
            list[0] = str;
            int i = 1;
            System.out.println("\nОТПРАВЛЯЕМ INGAME ИГРОКОВ");
            System.out.println("длина сета - " + Server.getIngamePlayersSet().size());
            System.out.println("длина массива - " + list.length);
            for (Player player : Server.getIngamePlayersSet()) {
                System.out.println("добавляем в ячейку массива №" + i + " - " + player.getLogin());
                list[i++] = player.getLogin();
            }
            outServerXML.send("INGAME PLAYERS", list);
        }
    }

    //действие на сообщение
    private void msgResult(String login, String msg) {
    }

    public String logoutResult(String login) {
        for (Player player : Server.getAllPlayersSet()){
            if (player.getLogin().equals(login) && player.getStatus().equals("online")){
                player.setStatus("offline");
                str = "success!";
                return str;
            }
        }
        str = "error, you already logged out";
        return str;
    }

    private void surrenderResult(String player) {
    }

    private void shootResult(String player, String x1, String y1, String x2, String y2) {
    }

    private void shipLocationResult(String player, String x1, String y1, String x2, String y2) {
    }

    //getters and setters

    public OutServerXML getOutServerXML() {
        return outServerXML;
    }

    public Player getThisPlayer() {
        return thisPlayer;
    }
    public void setThisPlayer(Player thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    public boolean isWaitingForReply() {
        return waitingForReply;
    }

    public GameController getGc() {
        return gc;
    }

    public void setGc(GameController gc) {
        this.gc = gc;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        inServerXML = new InServerXML(socket);
        outServerXML = new OutServerXML(socket);
    }
}
