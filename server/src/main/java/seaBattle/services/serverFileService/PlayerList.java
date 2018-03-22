package seaBattle.services.serverFileService;

import seaBattle.model.Player;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>PlayerList</code> exist for creating playerList.xml file
 * by marshalling object based on <code>PlayerList</code> class into XML
 * @author Oleksandr Symonenko
 */

@XmlRootElement(name = "playerList")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerList
{
    @XmlElement(name = "player")
    private List<Player> playerList = null;

    public List<Player> getPlayerList() {
        return playerList == null ? new ArrayList<>() : playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }
}
