package com.example.gameoflife;

import backend.Map;
import backend.Simulation;
import backend.Testing;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;

public class App extends Application {
    private static Stage stg;
    private Map mapWithBorders;
    private Map mapNoBorders;

    @FXML
    private TextField mapHeightTF;
    @FXML
    private TextField mapWidthTF;
    @FXML
    private TextField jungleRatioTF;
    @FXML
    private TextField animalsAmountTF;
    @FXML
    private TextField startEnergyTF;
    @FXML
    private TextField plantEnergyTF;
    @FXML
    private TextField moveEnergyTF;

    public void startSimulation() {

        mapWithBorders = new Map(
                Integer.parseInt(this.animalsAmountTF.getText()),
                Integer.parseInt(this.mapWidthTF.getText()),
                Integer.parseInt(this.mapHeightTF.getText()),
                Double.parseDouble(this.jungleRatioTF.getText()),
                true,
                Integer.parseInt(this.startEnergyTF.getText()),
                Integer.parseInt(this.moveEnergyTF.getText()),
                Double.parseDouble(this.plantEnergyTF.getText()));

        mapNoBorders = new Map(
                Integer.parseInt(this.animalsAmountTF.getText()),
                Integer.parseInt(this.mapWidthTF.getText()),
                Integer.parseInt(this.mapHeightTF.getText()),
                Double.parseDouble(this.jungleRatioTF.getText()),
                false,
                Integer.parseInt(this.startEnergyTF.getText()),
                Integer.parseInt(this.moveEnergyTF.getText()),
                Double.parseDouble(this.plantEnergyTF.getText()));

        mapsInit();
    }

    public void mapsInit(){
        VBox leftVbox = new VBox(new Text("Map with no borders"));
        VBox rightVbox = new VBox(new Text("Map with borders"));
        HBox hbox = new HBox(leftVbox,rightVbox);
        GridHandler leftGrid = new GridHandler(mapNoBorders);
        GridHandler rightGrid = new GridHandler(mapWithBorders);
        leftVbox.getChildren().add(leftGrid.getGridPane());
        rightVbox.getChildren().add(rightGrid.getGridPane());
        stg.setScene(new Scene(hbox,500,500));
        stg.setResizable(true);
        Thread mapWithBordersThread = new Thread(new Simulation(mapWithBorders, rightGrid));
        Thread mapNoBordersThread = new Thread(new Simulation(mapNoBorders, leftGrid));
        mapNoBordersThread.start();
        mapWithBordersThread.start();
//        Thread forTests = new Thread(new Testing("a"));
//        forTests.start();
    }


    @Override
    public void start(Stage stage) throws IOException {
        stg = stage;
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(new FileInputStream("src/main/resources/Main.fxml"));
        Scene scene = new Scene(root);
        Image image = new Image(new FileInputStream("src/main/resources/logo.jpg"));
        stage.getIcons().add(image);
        stage.setTitle("gameOfLife by @Viciooo");
        stage.setResizable(false);
        stage.setScene(scene);
        System.out.println(System.getProperty("javafx.version"));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}


