package seaBattle.model;

public class Game extends Thread {
    private Player firstPlayerThread;
    private Player secondPlayerThread;
    private Player currentPlayerThread;

    public Game(Player firstPlayerThread) {
        this.firstPlayerThread = firstPlayerThread;
    }

    public Game(Player firstPlayerThread, Player secondPlayerThread) {
        this.firstPlayerThread = firstPlayerThread;
        this.secondPlayerThread = secondPlayerThread;
    }

    public Player getFirstPlayerThread() {
        return firstPlayerThread;
    }

    public void setFirstPlayerThread(Player firstPlayerThread) {
        this.firstPlayerThread = firstPlayerThread;
    }

    public Player getSecondPlayerThread() {
        return secondPlayerThread;
    }

    public void setSecondPlayerThread(Player secondPlayerThread) {
        this.secondPlayerThread = secondPlayerThread;
    }

    public Player getCurrentPlayerThread() {
        return currentPlayerThread;
    }

    public void setCurrentPlayerThread(Player currentPlayerThread) {
        this.currentPlayerThread = currentPlayerThread;
    }
}
