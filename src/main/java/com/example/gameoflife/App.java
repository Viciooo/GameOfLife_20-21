package com.example.gameoflife;

import backend.Map;
import backend.Simulation;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
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
        VBox leftVbox = new VBox();
        VBox rightVbox = new VBox();
        HBox hbox = new HBox(leftVbox,rightVbox);
        GridHandler leftGrid = new GridHandler(mapNoBorders);
        GridHandler rightGrid = new GridHandler(mapWithBorders);
        leftVbox.getChildren().add(leftGrid.getGridPane());
        rightVbox.getChildren().add(rightGrid.getGridPane());
        stg.setScene(new Scene(hbox));
        stg.setMaximized(true);
        Simulation forMapNoBorders = new Simulation(mapNoBorders, leftGrid);
        Simulation forMapWithBorders = new Simulation(mapWithBorders, rightGrid);
//        forMapNoBorders.run();
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
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}


