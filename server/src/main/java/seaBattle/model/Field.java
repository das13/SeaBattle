package seaBattle.model;

public class Field {
    private int [][] field = new int[10][10];

    public int[][] getField() {
        return field;
    }

    public void setField(int[][] field) {


        this.field = field;
    }

    public void setShip (Ship ship) {
        int[] a = ship.getShip();
        if (ship.isVertical()) {
            for (int i = a[1]; i < a[3];i++) {
                field[a[0]][i] = 1;
            }
        }else {
            for (int i = a[0]; i < a[2];i++) {
                field[i][a[1]] = 1;
            }
        }
    }
}