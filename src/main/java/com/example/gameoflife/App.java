package com.example.gameoflife;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(new FileInputStream("src/main/resources/Main.fxml"));
        Scene scene = new Scene(root);
        Image image  = new Image(new FileInputStream("src/main/resources/logo.jpg"));
        stage.getIcons().add(image);
        stage.setTitle("gameOfLife by @Viciooo");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}