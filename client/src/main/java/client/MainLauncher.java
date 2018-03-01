package client;

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

public class MainLauncher extends Application {

    final static Logger logger = Logger.getLogger(MainLauncher.class);
    private static Stage primaryStageObj;

    public static Stage getPrimaryStageObj() {
        return primaryStageObj;
    }

    @Override
    public void start(Stage primaryStage){
        primaryStageObj = primaryStage;

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/views/regForm.fxml"));
            logger.info("Load regForm.fxml is successfully");

        } catch (IOException e) {
            logger.info("Can not load regForm.fxml", e);
            logger.error("Can not load regForm.fxml", e);
        }
        primaryStage.setTitle("Sea Battle 2018");
        Scene scene = new Scene(root,300,550);
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
