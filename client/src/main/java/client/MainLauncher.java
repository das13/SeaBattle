package client;

import client.controller.ServerListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *Class of the main application loader
 *@autor Dmytro Cherevko
 *@version 1.0
 */

public class MainLauncher extends Application {

    final static Logger logger = Logger.getLogger(MainLauncher.class);
    private static Stage primaryStageObj;
    private List<Parent> parents = new ArrayList<>();

    public static Stage getPrimaryStageObj() {
        return primaryStageObj;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStageObj = primaryStage;
        ServerListener listener = ServerListener.getListener();
        Parent root = null;
        try {
            parents.add(FXMLLoader.load(getClass().getResource("/views/regForm.fxml")));
            parents.add(FXMLLoader.load(getClass().getResource("/views/commonWindow.fxml")));
            parents.add(FXMLLoader.load(getClass().getResource("/views/GameWindow.fxml")));
            root = parents.get(0);
            logger.info("Load regForm.fxml is successfully");

        } catch (IOException e) {
            logger.error("Can not load regForm.fxml", e);
        }
        primaryStage.setTitle("Sea Battle 2018");
        Scene scene = new Scene(root, 300, 600);
        scene.getStylesheets().add(0, "css/main.css");
        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
