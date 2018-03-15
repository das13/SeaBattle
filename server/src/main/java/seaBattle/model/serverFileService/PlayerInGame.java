package seaBattle.model.serverFileService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>PlayerInGame</code> exist for use by <code>GameCondition</code>
  * when saves game
 * @author Oleksandr Symonenko
 */

@XmlRootElement(name = "player")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerInGame{
    String login;
    String turnTime;
    String row0, row1, row2, row3, row4, row5, row6, row7, row8, row9;
    String[] row = new String[10];

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String isTurnTime() {
        return turnTime;
    }
    public void setTurnTime(String turnTime) {
        this.turnTime = turnTime;
    }

    public String getRow0() {
        return row0;
    }
    public void setRow0(String row0) {
        this.row0 = row0;
    }

    public String getRow1() {
        return row1;
    }
    public void setRow1(String row1) {
        this.row1 = row1;
    }

    public String getRow2() {
        return row2;
    }
    public void setRow2(String row2) {
        this.row2 = row2;
    }

    public String getRow3() {
        return row3;
    }
    public void setRow3(String row3) {
        this.row3 = row3;
    }

    public String getRow4() {
        return row4;
    }
    public void setRow4(String row4) {
        this.row4 = row4;
    }

    public String getRow5() {
        return row5;
    }
    public void setRow5(String row5) {
        this.row5 = row5;
    }

    public String getRow6() {
        return row6;
    }
    public void setRow6(String row6) {
        this.row6 = row6;
    }

    public String getRow7() {
        return row7;
    }
    public void setRow7(String row7) {
        this.row7 = row7;
    }

    public String getRow8() {
        return row8;
    }
    public void setRow8(String row8) {
        this.row8 = row8;
    }

    public String getRow9() {
        return row9;
    }
    public void setRow9(String row9) {
        this.row9 = row9;
    }

    public String[] getRow() {
        return row;
    }
    public void setRow(String[] row) {
        this.row = row;
    }
}
