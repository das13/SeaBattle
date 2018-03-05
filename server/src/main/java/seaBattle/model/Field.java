package seaBattle.model;

public class Field {
    private int [][] field = new int[10][10];
    private int [] countOfShip = new int[4];
    String str;

    public int[] getCountOfShip() {
        return countOfShip;
    }

    public int[][] getField() {
        return field;
    }

    public void setField(int[][] field) {

        this.field = field;
    }

    public String setShip (Ship ship) {
        int[] a = ship.getShip();
        if (shipCountChecking(ship)) {
            if (ship.isVertical()) {
                if (areaChecking(field, a[1], a[3], ship.isVertical())) {
                    for (int i = a[1]; i <= a[3]; i++) {
                        field[a[0]][i] = 1;
                        setSafeArea(field, a[0], i, ship.isVertical());
                    }
                    countOfShip[ship.getHealth()-1]++;
                    str = "OK";
                }else {
                    str = "PLACE ERROR";
                }
            } else {
                if (areaChecking(field, a[0], a[2], ship.isVertical())) {
                    for (int i = a[0]; i <= a[2]; i++) {
                        field[i][a[1]] = 1;
                        setSafeArea(field, i, a[1], ship.isVertical());
                    }
                    countOfShip[ship.getHealth()-1]++;
                    str = "OK";
                }else {
                    str = "PLACE ERROR";
                }
            }
        }else {
            str="COUNT ERROR";
        }
        return str;
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

    public boolean shipCountChecking(Ship ship) {
        if (ship.getHealth() == 4 && countOfShip[3] < 1) {
            return true;
        }else if (ship.getHealth() == 3 && countOfShip[2] < 2) {
            return true;
        }else if (ship.getHealth() == 2 && countOfShip[1] < 3) {
            return true;
        }else if (ship.getHealth() == 1 && countOfShip[0] < 4) {
            return true;
        }else {
            return false;
        }
    }
}