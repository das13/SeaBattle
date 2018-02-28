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

public class Main extends Application {

    final static Logger logger = Logger.getLogger(Main.class);
    private static Stage primaryStageObj;

    public static Stage getPrimaryStageObj() {
        return primaryStageObj;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStageObj = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/views/regForm.fxml"));
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
