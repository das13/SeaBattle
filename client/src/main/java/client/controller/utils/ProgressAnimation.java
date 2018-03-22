package client.controller.utils;

import javafx.concurrent.Task;

/**
 *class extends Task<Integer> for creating Task for animation of progressBar
 *@author Dmytro Cherevko
 *@version 1.0
 */

public class ProgressAnimation extends Task<Integer> {

    @Override
    protected Integer call() throws Exception {
        for (int i = 0; i <= 100; i++) {
            updateProgress(i/100.0 + 0.01, 1.0);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isCancelled()) {return i;}
        }
        return 100;
    }

    @Override
    protected void updateProgress(double workDone, double max) {
        super.updateProgress(workDone, max);
    }
}
