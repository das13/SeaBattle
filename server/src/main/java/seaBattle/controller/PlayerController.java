package seaBattle.controller;

import org.apache.log4j.Logger;
import seaBattle.model.Player;
import seaBattle.Server;
import seaBattle.model.Ship;
import seaBattle.model.Status;
import seaBattle.services.playerControllerService.AdministrationService;
import seaBattle.services.playerControllerService.AuthorizationService;
import seaBattle.services.playerControllerService.GameCreationService;
import seaBattle.services.playerControllerService.ServerService;
import seaBattle.services.xmlService.InServerXML;
import seaBattle.services.xmlService.OutServerXML;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.net.Socket;

/**
 * <code>PlayerController</code> is the main communication unit that
 * handles thread with client, receives messages and provides answers.
 * Though <code>PlayerController</code> user do all actions related to Server part of game.
 * @author Oleksandr Symonenko
 */

public class PlayerController extends Thread {

    private Socket socket;
    private String str;
    private InServerXML inServerXML;
    private OutServerXML outServerXML;
    private Player thisPlayer;
    private GameController gc;
    private boolean waitingForReply;
    private AdministrationService adS;
    private AuthorizationService auS;
    private GameCreationService gcS;
    private ServerService sS;
    private final static Logger logger = Logger.getLogger(PlayerController.class);

    /**
     * <code>PlayerController</code> constructor
     */
    public PlayerController() {
        thisPlayer = new Player();
        adS = new AdministrationService(this);
        auS = new AuthorizationService(this);
        gcS = new GameCreationService(this);
        sS = new ServerService(this);
    }

    /**
     * <code>run</code> adds PlayerController thread ability to receive XML data
     * from linked Client and send appropriate XML data answers, based on keys/values in incoming XMLs
     */
    public void run() {
        thisPlayer.setStatus(Status.OFFLINE);
        try {
            while (!socket.isClosed()){
                inServerXML.setReader(inServerXML.getFactory().createXMLStreamReader(inServerXML.getFileReader()));
                XMLStreamReader reader = inServerXML.getReader();
                while (reader.hasNext()) {
                    if (reader.getEventType() == 1 && "key".equals(reader.getLocalName())){
                        reader.next();
                        switch (inServerXML.checkValue(reader)){
                            case "LOG IN": {
                                String login = inServerXML.checkValue(reader);
                                thisPlayer.setLogin(login);
                                String password = inServerXML.checkValue(reader);
                                thisPlayer.setPassword(password);
                                outServerXML.send("LOG IN", auS.authResult(login,password));
                                sS.updateAndSendPlayersInfo();
                                break;
                            }
                            case "LOG OUT": {
                                String login = inServerXML.checkValue(reader);
                                outServerXML.send("LOG OUT", auS.logoutResult(login));
                                sS.updateAndSendPlayersInfo();
                                break;
                            }
                            case "REG": {
                                String login = inServerXML.checkValue(reader);
                                String password = inServerXML.checkValue(reader);
                                outServerXML.send("REG", auS.regResult(login,password));
                                Server.updateAllPlayersSet();
                                break;
                            }
                            case "INVITE": {
                                waitingForReply = true;
                                String player1 = inServerXML.checkValue(reader);
                                String player2 = inServerXML.checkValue(reader);
                                gcS.inviteResult(player1,player2);
                                break;
                            }
                            case "REPLY": {
                                String player1 = inServerXML.checkValue(reader);
                                String reply = inServerXML.checkValue(reader);
                                str = gcS.replyResult(player1,reply);
                                if ("OK".equals(str)) {
                                    sS.updateAndSendPlayersInfo();
                                }
                                break;
                            }
                            case "SHIP LOCATION": {
                                int x1 = Integer.parseInt(inServerXML.checkValue(reader));
                                int y1 = Integer.parseInt(inServerXML.checkValue(reader));
                                int x2 = Integer.parseInt(inServerXML.checkValue(reader));
                                int y2 = Integer.parseInt(inServerXML.checkValue(reader));
                                getOutServerXML().send("SHIP LOCATION", gc.setShip(this, new Ship(new int[]{x1, y1, x2, y2})));
                                if (gc.checkStartGame()) {
                                    gc.getPlayerController1().getOutServerXML().send("START GAME","READY");
                                    gc.getPlayerController2().getOutServerXML().send("START GAME","READY");
                                    gc.changeCurrentPlayer();
                                    gc.startTimer();
                                }
                                System.out.println();
                                break;
                            }
                            case "SHOOT": {
                                String x1 = inServerXML.checkValue(reader);
                                String y1 = inServerXML.checkValue(reader);
                                getOutServerXML().send("SHOOT RESULT",gc.shoot(this,Integer.parseInt(x1),Integer.parseInt(y1)));
                                break;
                            }
                            case "SURRENDER": {
                                String player = inServerXML.checkValue(reader);
                                gc.surrender(this);
                                break;
                            }
                            case "MSG": {
                                String msg = inServerXML.checkValue(reader);
                                sS.msgResult(msg);
                                break;
                            }
                            case "BAN PLAYER": {
                                String playerToBan = InServerXML.checkValue(reader);
                                String result = adS.banPlayerResult(playerToBan);
                                if ((result).contains("kicked")) {
                                    sS.updateAndSendPlayersInfo();
                                }
                                sS.msgServer(result);
                                break;
                            }
                            case "BAN IP": {
                                String ipToBan = InServerXML.checkValue(reader);
                                String result = adS.banIpResult(ipToBan);
                                if ((result).contains("kicked")) {
                                    sS.updateAndSendPlayersInfo();
                                }
                                sS.msgServer(result);
                                break;
                            }
                            case "UNBAN PLAYER": {
                                String playerToUnBan = InServerXML.checkValue(reader);
                                String result = adS.unbanPlayerResult(playerToUnBan);
                                sS.msgServer(result);
                                break;
                            }
                            case "UNBAN IP": {
                                String ipToUnBan = InServerXML.checkValue(reader);
                                String result = adS.unbanIpResult(ipToUnBan);
                                sS.msgServer(result);
                                break;
                            }
                            case "REBOOT": {
                                String admin = inServerXML.checkValue(reader);
                                sS.msgServer("Server rebooting by " + admin);
                                adS.rebootServer(admin);
                                break;
                            }
                            case "SHUTDOWN": {
                                String admin = inServerXML.checkValue(reader);
                                sS.msgServer("Server is shutting down by " + admin);
                                adS.shutdownServer(admin);
                                break;
                            }
                            default:{
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
            Server.getAllPlayersControllerSet().remove(this);
            auS.logoutResult(thisPlayer.getLogin());
        } catch (XMLStreamException e) {
            logger.error("Receive/Send error between thread and client", e);
        } catch (IOException e) {
            logger.error("Kick player by ip error", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Error with clothing thread", e);
            }
        }
    }

    public OutServerXML getOutServerXML() {
        return outServerXML;
    }

    public Player getThisPlayer() {
        return thisPlayer;
    }

    public boolean isWaitingForReply() {
        return waitingForReply;
    }

    public void setGc(GameController gc) {
        this.gc = gc;
    }

    public GameController getGc() {
        return gc;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        inServerXML = new InServerXML(socket);
        outServerXML = new OutServerXML(socket);
    }

    public Socket getSocket() {
        return socket;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerController that = (PlayerController) o;

        if (waitingForReply != that.waitingForReply) return false;
        if (socket != null ? !socket.equals(that.socket) : that.socket != null) return false;
        return thisPlayer != null ? thisPlayer.equals(that.thisPlayer) : that.thisPlayer == null;
    }

    @Override
    public int hashCode() {
        int result = socket != null ? socket.hashCode() : 0;
        result = 31 * result + (thisPlayer != null ? thisPlayer.hashCode() : 0);
        result = 31 * result + (waitingForReply ? 1 : 0);
        return result;
    }
}
