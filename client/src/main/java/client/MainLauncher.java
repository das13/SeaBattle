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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *Class of the main application loader
 *@autor Dmytro Cherevko
 *@version 1.0
 */

public class MainLauncher extends Application {

    private final static Logger logger = Logger.getLogger(MainLauncher.class);
    private static Stage primaryStageObj;
    private List<Parent> parents = new ArrayList<>();
    private static Properties propertyForms;

    public static Stage getPrimaryStageObj() {
        return primaryStageObj;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStageObj = primaryStage;
        ServerListener listener = ServerListener.getListener();
        Parent root = null;
        try {
            parents.add(FXMLLoader.load(getClass().getResource(propertyForms.getProperty("regForm"))));
            parents.add(FXMLLoader.load(getClass().getResource(propertyForms.getProperty("commonWindow"))));
            parents.add(FXMLLoader.load(getClass().getResource(propertyForms.getProperty("GameWindow"))));
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
        loadProperty("form.properties");
        launch(args);
    }

    /**
     * Method for loading property from file
     * @param fileName
     */
    private static void loadProperty(String fileName) {
        propertyForms = new Properties();
        InputStream in = MainLauncher.class.getClassLoader().getResourceAsStream(fileName);
        try {
            propertyForms.load(in);
        } catch (IOException e) {
            logger.error("Can not load " + fileName, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("Can not close InputStream in loadProperty(String fileName)", e);
                }
            }
        }
    }

    public static Properties getPropertyForms() {
        return propertyForms;
    }
}
