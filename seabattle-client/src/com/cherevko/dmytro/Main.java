package com.cherevko.dmytro;

import com.cherevko.dmytro.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/FXMLForms/regForm.fxml"));
        primaryStage.setTitle("Sea Battle 2018");
        Scene scene = new Scene(root,300,400);
        scene.getStylesheets().add(0, "resources/css/main.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
        try ( Socket socket = new Socket("localhost", 9001)) {

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
