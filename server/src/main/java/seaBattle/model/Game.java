package seaBattle.model;

public class Game extends Thread {
    private Field field1;
    private Field field2;

    public Field getField1() {
        return field1;
    }

    public void setField1(Field field1) {
        this.field1 = field1;
    }

    public Field getField2() {
        return field2;
    }

    public void setField2(Field field2) {
        this.field2 = field2;
    }
}
