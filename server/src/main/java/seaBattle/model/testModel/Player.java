package seaBattle.model.testModel;

public class Player {

    private Field field;
    private String name;

    public Player(String name) {
        this.name = name;
    }

    //getters & setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Field getField() {
        return field;
    }
    public void setField(Field field) {
        this.field = field;
    }
}
