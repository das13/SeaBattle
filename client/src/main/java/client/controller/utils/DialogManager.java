package client.controller.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Window;

/**
 *class for creating alert windows
 *@author Dmytro Cherevko
 *@version 1.0
 */

public class DialogManager {

    /**
     * Created window with information
     * @param window current window
     * @param title title of alert-window
     * @param text text of alert-window
     */
    public static void showInfoDialog(Window window, String title, String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setContentText(text);
                alert.setHeaderText("INFO");
                alert.setX(window.getX() + 50);
                alert.setY(window.getY() + 200);
                alert.showAndWait();
            }
        });
    }

    /**
     * Created window with information about error
     * @param window current window
     * @param title title of alert-window
     * @param text text of alert-window
     */
    public static void showErrorDialog(Window window, String title, String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setContentText(text);
                alert.setHeaderText("ERROR");
                alert.setX(window.getX() + 50);
                alert.setY(window.getY() + 200);
                alert.showAndWait();
            }
        });
    }
}

