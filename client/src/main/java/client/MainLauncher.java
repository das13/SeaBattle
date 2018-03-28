package client;

import client.controller.PropertiesLoader;
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
import java.util.Properties;

/**
 *Class of the main application loader
 *@author Dmytro Cherevko
 *@version 1.0
 */

public class MainLauncher extends Application {

    private final static Logger logger = Logger.getLogger(MainLauncher.class);
    private static Stage primaryStageObj;
    private static Properties propertyForms;

    public static Stage getPrimaryStageObj() {
        return primaryStageObj;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStageObj = primaryStage;
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(propertyForms.getProperty("regForm")));
            logger.info("Load regForm.fxml is successfully");
        } catch (IOException e) {
            logger.error("Can not load regForm.fxml", e);
        }
        primaryStage.setTitle("Sea Battle 2018");
        if (root != null) {
            Scene scene = new Scene(root, 300, 600);
            scene.getStylesheets().add(0, "css/main.css");
            scene.setRoot(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
        }
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
        PropertiesLoader pl = new PropertiesLoader();
        propertyForms = pl.loadProperty("form.properties");
        launch(args);
    }

    public static Properties getPropertyForms() {
        return propertyForms;
    }
}
