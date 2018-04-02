package seaBattle.model;

import seaBattle.Server;

/**
 * logic and model of field of sea battle
 * logic of ships setting
 * logic of shoot by opponent
 * @author Roman Kraskovskiy
 */
public class Field {
    private int [][] field = new int[10][10];
    private int [] countOfShip = new int[4];
    private String str;

    /**
     * @return field of player
     */
    public int[][] getField() {
        return field;
    }

    /**
     * shoot of your opponent
     * @param x row
     * @param y col
     * @return result of shoot
     */
    public String shoot (int x, int y) {
        if (field[x][y] == 1 && checkShipDestroy(x,y)) {
            field[x][y] = 3;
            str = "DESTROY";
        } else if (field[x][y] == 1 && !checkShipDestroy(x,y)) {
            field[x][y] = 3;
            str = "HIT";
        } else {
            str = "MISS";
        }
        return str;
    }

    /**
     * checking of ship destroy after shooting
     * @param x row
     * @param y col
     * @return true if destroy
     */
    public boolean checkShipDestroy(int x,int y) {
        int c = 0;
        if (y >= 0 && y < 10 && x >= 0 && x < 10) {
            if (x + 1 > 9 || field[x + 1][y] != 1) {
                c++;
            }
            if (x - 1 < 0 || field[x - 1][y] != 1) {
                c++;
            }
            if (y + 1 > 9 || field[x][y + 1] != 1) {
                c++;
            }
            if (y - 1 < 0 || field[x][y - 1] != 1) {
                c++;
            }
            if (c == 4) {
                if (!(x + 2 > 9 || x + 1 > 9) && field[x + 2][y] == 1 && field[x + 1][y] == 3) {
                    c--;
                }
                if (!(x - 2 < 0 || x - 1 < 0) && field[x - 2][y] == 1 && field[x - 1][y] == 3) {
                    c--;
                }
                if (!(y + 2 > 9 || y + 1 > 9) && field[x][y + 2] == 1 && field[x][y + 1] == 3) {
                    c--;
                }
                if (!(y - 2 < 0 || y - 1 < 0) && field[x][y - 2] == 1 && field[x][y - 1] == 3) {
                    c--;
                }
            }
            if (c == 4) {
                if (!(x + 3 > 9 || x + 2 > 9 || x + 1 > 9) && field[x + 3][y] == 1 && field[x + 2][y] == 3 &&
                        field[x + 1][y] == 3) {
                    c--;
                }
                if (!(x - 3 < 0 || x - 2 < 0 || x - 1 < 0) && field[x - 3][y] == 1 && field[x - 2][y] == 3 &&
                        field[x - 1][y] == 3) {
                    c--;
                }
                if (!(y + 3 > 9 || y + 2 > 9 || y + 1 > 9) && field[x][y + 3] == 1 && field[x][y + 2] == 3 &&
                        field[x][y + 1] == 3) {
                    c--;
                }
                if (!(y - 3 < 0 || y - 2 < 0 || y - 1 < 0) && field[x][y - 3] == 1 && field[x][y - 2] == 3 &&
                        field[x][y - 1] == 3) {
                    c--;
                }
            }
        }
        if (c == 4) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ship setting
     * @param ship ship for setting
     * @return result of ship setting
     */
    public String setShip (Ship ship) {
        int[] a = ship.getShip();
        if (checkOutOfBoundsShip(ship)) {
            if (checkLongShip(ship)) {
                if (shipCountChecking(ship)) {
                    if (ship.isVertical()) {
                        if (areaChecking(a[0], a[1], a[3], ship.isVertical())) {
                            for (int i = a[1]; i <= a[3]; i++) {
                                field[a[0]][i] = 1;
                                setSafeArea(field, a[0], i);
                            }
                            countOfShip[ship.getHealth() - 1]++;
                            return "OK";
                        } else {
                            return "you can not to place ship there";
                        }
                    } else {
                        if (areaChecking(a[1], a[0], a[2], ship.isVertical())) {
                            for (int i = a[0]; i <= a[2]; i++) {
                                field[i][a[1]] = 1;
                                setSafeArea(field, i, a[1]);
                            }
                            countOfShip[ship.getHealth() - 1]++;
                            return "OK";
                        } else {
                            return "you can not to place ship there";
                        }
                    }
                } else {
                    return "count of this ship(s) is enough";
                }
            } else {
                return "don't cheat please (your ship is so long)";
            }
        } else {
            return "you can not to place ship there";
        }
    }

    /**
     * checking of ship length for constraint
     * @param ship ship for checking
     * @return true if ship not more than the specified length
     */
    public boolean checkLongShip(Ship ship) {
        if (ship.getHealth() > 4) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * checking ship out of bound
     * @param ship ship for checking
     * @return true if ship doesn't abroad
     */
    public boolean checkOutOfBoundsShip(Ship ship) {
        int [] a = ship.getShip();
        if (ship.isVertical() && a[2]>9) {
            return false;
        }else if (!ship.isVertical() && a[3]>9){
            return false;
        }
        return true;
    }

    /**
     * set area where ships cannot be placed
     * @param f field
     * @param i1 row
     * @param j1 col
     */
    public void setSafeArea(int [][]f, int i1,int j1) {
        for (int i = i1 - 1; i < i1 + 2; i++) {
            for (int j = j1 -1 ; j< j1 + 2; j++) {
                if(i >= 0 && i<=9 && j >= 0 && j<=9) {
                    if (f[i][j] != 1) {
                        f[i][j] = 2;
                    }
                }
            }
        }
    }

    /**
     * checking area for placing ship
     * @param stat static col or row
     * @param a start row or col
     * @param b end row or col
     * @param v vertical or not vertical ship for determining rows and cols
     * @return result of checking
     */
    public boolean areaChecking(int stat,int a,int b,boolean v) {
        if (stat >= 0 && stat < 10 && a >= 0 && b < 10) {
            if (v) {
                for (int i = a; i <= b; i++) {
                    if (field[stat][i] != 0) {
                        return false;
                    }

                }
            } else {
                for (int i = a; i <= b; i++) {
                    if (field[i][stat] != 0) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * checking count of different kinds of ships for control setting ships
     * @param ship ship for checking
     * @return true if player can to place ship
     */
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