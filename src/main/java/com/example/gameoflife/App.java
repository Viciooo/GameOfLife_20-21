package com.example.gameoflife;

import backend.Map;
import backend.Simulation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
                Double.parseDouble(this.plantEnergyTF.getText()),
                false);

        mapNoBorders = new Map(
                Integer.parseInt(this.animalsAmountTF.getText()),
                Integer.parseInt(this.mapWidthTF.getText()),
                Integer.parseInt(this.mapHeightTF.getText()),
                Double.parseDouble(this.jungleRatioTF.getText()),
                false,
                Integer.parseInt(this.startEnergyTF.getText()),
                Integer.parseInt(this.moveEnergyTF.getText()),
                Double.parseDouble(this.plantEnergyTF.getText()),
                false);

        mapsInit();
    }


    public void mapsInit() {
        VBox leftVbox = new VBox(new Text(mapNoBorders.toString()));
        VBox rightVbox = new VBox(new Text(mapWithBorders.toString()));
        Button startStopLeft = new Button("start/stop");
        Button startStopRight = new Button("start/stop");
        HBox boards = new HBox(leftVbox, rightVbox);
        HBox buttonsContainer = new HBox(startStopLeft, startStopRight);
        VBox buttonsAndBoardsContainer = new VBox(boards, buttonsContainer);

        startStopLeft.setOnAction(event -> {
            this.mapNoBorders.swapRunning();
        });
        startStopRight.setOnAction(event -> this.mapWithBorders.swapRunning());

        GridHandler leftGrid = new GridHandler(mapNoBorders);
        ChartHandler leftChart = new ChartHandler(mapNoBorders);
        GridHandler rightGrid = new GridHandler(mapWithBorders);
        ChartHandler rightChart = new ChartHandler(mapWithBorders);
        leftVbox.getChildren().addAll(leftGrid.getGridPane(), leftChart.createChart());
        rightVbox.getChildren().addAll(rightGrid.getGridPane(), rightChart.createChart());
        stg.setScene(new Scene(buttonsAndBoardsContainer, 500, 500));
        stg.setResizable(true);
        Thread mapWithBordersThread = new Thread(new Simulation(mapWithBorders, rightGrid, rightChart));
        Thread mapNoBordersThread = new Thread(new Simulation(mapNoBorders, leftGrid, leftChart));
        mapNoBordersThread.start();
        mapWithBordersThread.start();
    }


    @Override
    public void start(Stage stage) throws IOException {
        stg = stage;
        stg.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
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


