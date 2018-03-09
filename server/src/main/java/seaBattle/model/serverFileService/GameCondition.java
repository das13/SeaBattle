package seaBattle.model.serverFileService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gameCondition")
@XmlAccessorType(XmlAccessType.FIELD)
public class GameCondition {

    String player1, player2;
    int row10, row11, row12, row13, row14, row15, row16, row17, row18, row19, row20, row21, row22, row23, row24, row25, row26, row27, row28, row29;
    int[] rows1 = new int[9];
    int[] rows2 = new int[9];

    public void serAllRows1(int[] rows1){
        this.rows1 = rows1;
        rows1[0] = row10;
        rows1[1] = row11;
        rows1[2] = row12;
        rows1[3] = row13;
        rows1[4] = row14;
        rows1[5] = row15;
        rows1[6] = row16;
        rows1[7] = row17;
        rows1[8] = row18;
        rows1[9] = row19;
    }

    public void serAllRows2(int[] rows2){
        this.rows2 = rows2;
        rows2[0] = row20;
        rows2[1] = row21;
        rows2[2] = row22;
        rows2[3] = row23;
        rows2[4] = row24;
        rows2[5] = row25;
        rows2[6] = row26;
        rows2[7] = row27;
        rows2[8] = row28;
        rows2[9] = row29;
    }

    public String getPlayer1() {
        return player1;
    }
    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public int getRow10() {
        return row10;
    }
    public void setRow10(int row10) {
        this.row10 = row10;
    }

    public int getRow11() {
        return row11;
    }
    public void setRow11(int row11) {
        this.row11 = row11;
    }

    public int getRow12() {
        return row12;
    }
    public void setRow12(int row12) {
        this.row12 = row12;
    }

    public int getRow13() {
        return row13;
    }
    public void setRow13(int row13) {
        this.row13 = row13;
    }

    public int getRow14() {
        return row14;
    }
    public void setRow14(int row14) {
        this.row14 = row14;
    }

    public int getRow15() {
        return row15;
    }
    public void setRow15(int row15) {
        this.row15 = row15;
    }

    public int getRow16() {
        return row16;
    }
    public void setRow16(int row16) {
        this.row16 = row16;
    }

    public int getRow17() {
        return row17;
    }
    public void setRow17(int row17) {
        this.row17 = row17;
    }

    public int getRow18() {
        return row18;
    }
    public void setRow18(int row18) {
        this.row18 = row18;
    }

    public int getRow19() {
        return row19;
    }
    public void setRow19(int row19) {
        this.row19 = row19;
    }

    public int getRow20() {
        return row20;
    }
    public void setRow20(int row20) {
        this.row20 = row20;
    }

    public int getRow21() {
        return row21;
    }
    public void setRow21(int row21) {
        this.row21 = row21;
    }

    public int getRow22() {
        return row22;
    }
    public void setRow22(int row22) {
        this.row22 = row22;
    }

    public int getRow23() {
        return row23;
    }
    public void setRow23(int row23) {
        this.row23 = row23;
    }

    public int getRow24() {
        return row24;
    }
    public void setRow24(int row24) {
        this.row24 = row24;
    }

    public int getRow25() {
        return row25;
    }
    public void setRow25(int row25) {
        this.row25 = row25;
    }

    public int getRow26() {
        return row26;
    }
    public void setRow26(int row26) {
        this.row26 = row26;
    }

    public int getRow27() {
        return row27;
    }
    public void setRow27(int row27) {
        this.row27 = row27;
    }

    public int getRow28() {
        return row28;
    }
    public void setRow28(int row28) {
        this.row28 = row28;
    }

    public int getRow29() {
        return row29;
    }
    public void setRow29(int row29) {
        this.row29 = row29;
    }

    public int[] getRows1() {
        return rows1;
    }
    public void setRows1(int[] rows1) {
        this.rows1 = rows1;
    }

    public int[] getRows2() {
        return rows2;
    }
    public void setRows2(int[] rows2) {
        this.rows2 = rows2;
    }
}
