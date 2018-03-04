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
            if (areaChecking(field,a[1],a[3],ship.isVertical())) {
                for (int i = a[1]; i <= a[3]; i++) {
                        field[a[0]][i] = 1;
                        setSafeArea(field, a[0], i, ship.isVertical());
                }
            }
        }else {
            if (areaChecking(field,a[0],a[2],ship.isVertical())) {
                for (int i = a[0]; i <= a[2]; i++) {
                        field[i][a[1]] = 1;
                        setSafeArea(field, i, a[i], ship.isVertical());
                }
            }
        }
    }

    public void setSafeArea(int [][]f, int i1,int j1,boolean v) {
        for (int i = i1 - 1; i < i1 + 2; i++) {
            for (int j = j1 -1 ; j< j1 + 2; j++) {
                try {
                    if (f[i][j] != 1) {
                        f[i][j] = 2;
                    }
                }catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("none");
                }
            }
        }
    }

    public boolean areaChecking(int [][]f,int a,int b,boolean v) {
        if (v) {
            for (int i = a; i <= b;i++) {
                if (field[a][i] != 0) {
                    System.out.println("U cant to place ship");
                    return false;
                }

            }
        }else {
            for (int i = a; i <=b;i++) {
                if (field[i][a] != 0) {
                    System.out.println("U cant to place ship");
                    return false;
                }
            }
        }
        return true;
    }
}