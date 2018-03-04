package seaBattle.model;

public class FieldTest {
    public static void main(String [] args) {
        Field field = new Field();
        field.setShip(new Ship(new int[]{0,2,0,4}));
        field.setShip(new Ship(new int[]{1,2,3,2}));
        for (int i=0;i<10;i++) {
            for (int j=0;j<10;j++) {
                System.out.print(field.getField()[i][j] + " ");
            }
            System.out.println("");
        }
    }
}
