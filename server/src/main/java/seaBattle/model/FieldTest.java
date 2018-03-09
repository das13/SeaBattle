package seaBattle.model;

public class FieldTest {
    public static void main(String [] args) {
        Field field = new Field();
        field.setShip(new Ship(new int[]{0,9,0,10}));
        field.setShip(new Ship(new int[]{2,2,4,2}));
        field.setShip(new Ship(new int[]{2,5,4,5}));
        for (int i=0;i<10;i++) {
            for (int j=0;j<10;j++) {
                System.out.print(field.getField()[i][j] + " ");
            }
            System.out.println("");
        }
        for (int i =0;i<4;i++) {
            System.out.println("Count of " + (i+1) + "ship = " + field.getCountOfShip()[i]);
        }
    }
}
