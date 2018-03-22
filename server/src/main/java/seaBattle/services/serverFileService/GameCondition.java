package seaBattle.services.serverFileService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * <code>GameCondition</code> exist for creating game-"player#1"VS"player#2".xml file
 * by marshalling object based on <code>GameCondition</code> class into XML
 * when saves game
 * @author Oleksandr Symonenko
 */

@XmlRootElement(name = "gameCondition")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameCondition {

        @XmlElement(name = "player")
        private List<PlayerInGame> playerInGameList = null;

    public List<PlayerInGame> getPlayerInGameList() {
        return playerInGameList;
    }
    public void setPlayerInGameList(List<PlayerInGame> playerInGameList) {
        this.playerInGameList = playerInGameList;
    }
}
