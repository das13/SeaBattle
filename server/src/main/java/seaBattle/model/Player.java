package seaBattle.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * model of player
 * @author Roman Kraskovskiy
 */
@XmlRootElement(name = "player")
@XmlAccessorType(XmlAccessType.FIELD)
public class Player {

    private String login;
    private String password;
    private String status;
    private int rank;

    /**
     * @return player login
     */
    public String getLogin() {
        return login;
    }

    /**
     * set login for player
     * @param login player login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return player password
     */
    public String getPassword() {
        return password;
    }

    /**
     * set password for player
     * @param password player password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return player rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * set rank for player
     * @param rank player rank
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * @return player status
     */
    public String getStatus() {
        return status;
    }

    /**
     * set status for player
     * @param status player status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
