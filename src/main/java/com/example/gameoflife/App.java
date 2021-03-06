package com.example.gameoflife;

import backend.Map;
import backend.Simulation;
import backend.StatisticsWriter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
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

    @FXML
    private CheckBox mapWithBordersMagicOn;
    @FXML
    private CheckBox mapNoBordersMagicOn;

    public void startSimulation() throws Exception {

        mapWithBorders = new Map(
                Integer.parseInt(this.animalsAmountTF.getText()),
                Integer.parseInt(this.mapWidthTF.getText()),
                Integer.parseInt(this.mapHeightTF.getText()),
                Double.parseDouble(this.jungleRatioTF.getText()),
                true,
                Integer.parseInt(this.startEnergyTF.getText()),
                Integer.parseInt(this.moveEnergyTF.getText()),
                Double.parseDouble(this.plantEnergyTF.getText()),
                false,
                this.mapWithBordersMagicOn.isSelected());

        mapNoBorders = new Map(
                Integer.parseInt(this.animalsAmountTF.getText()),
                Integer.parseInt(this.mapWidthTF.getText()),
                Integer.parseInt(this.mapHeightTF.getText()),
                Double.parseDouble(this.jungleRatioTF.getText()),
                false,
                Integer.parseInt(this.startEnergyTF.getText()),
                Integer.parseInt(this.moveEnergyTF.getText()),
                Double.parseDouble(this.plantEnergyTF.getText()),
                false,
                this.mapNoBordersMagicOn.isSelected());

        mapsInit();
    }


    public void mapsInit() {
        VBox leftVbox = new VBox(new Text(mapNoBorders.toString()));
        VBox rightVbox = new VBox(new Text(mapWithBorders.toString()));
        Button startStopLeft = new Button("start/stop");
        Button startStopRight = new Button("start/stop");
        HBox boards = new HBox(leftVbox, rightVbox);
        HBox buttonsContainer = new HBox(startStopLeft, startStopRight);
        boards.setAlignment(Pos.CENTER);
        boards.setSpacing(300);
        leftVbox.setSpacing(10);
        rightVbox.setSpacing(10);
        VBox buttonsAndBoardsContainer = new VBox(boards, buttonsContainer);
        ScrollPane wholeWindow = new ScrollPane(buttonsAndBoardsContainer);
        wholeWindow.setFitToWidth(true);
        wholeWindow.setFitToHeight(true);

        startStopLeft.setOnAction(event -> this.mapNoBorders.swapRunning());
        startStopRight.setOnAction(event -> this.mapWithBorders.swapRunning());

        TrackedAnimalHandler leftTrackedAnimal = new TrackedAnimalHandler(mapNoBorders);
        GridHandler leftGrid = new GridHandler(mapNoBorders);
        ChartHandler leftChart = new ChartHandler(mapNoBorders);
        GenomeDominantHandler leftGenomeDominant = new GenomeDominantHandler(mapNoBorders);
        StatisticsWriter leftWriter = new StatisticsWriter(mapNoBorders);

        TrackedAnimalHandler rightTrackedAnimal = new TrackedAnimalHandler(mapNoBorders);
        GridHandler rightGrid = new GridHandler(mapWithBorders);
        ChartHandler rightChart = new ChartHandler(mapWithBorders);
        GenomeDominantHandler rightGenomeDominant = new GenomeDominantHandler(mapWithBorders);
        StatisticsWriter rightWriter = new StatisticsWriter(mapWithBorders);


        leftVbox.getChildren().addAll(
                leftGrid.getGridPane(),
                leftChart.createChart(),
                leftGenomeDominant.createLabel(),
                leftTrackedAnimal.createLabel());

        rightVbox.getChildren().addAll(
                rightGrid.getGridPane(),
                rightChart.createChart(),
                rightGenomeDominant.createLabel(),
                rightTrackedAnimal.createLabel());

        stg.setScene(new Scene(wholeWindow));
        stg.setResizable(true);
        stg.setMaximized(true);

        Thread mapWithBordersThread = new Thread(new Simulation(
                mapWithBorders,
                rightGrid,
                rightChart,
                rightGenomeDominant,
                rightTrackedAnimal,
                rightWriter));

        Thread mapNoBordersThread = new Thread(new Simulation(
                mapNoBorders,
                leftGrid,
                leftChart,
                leftGenomeDominant,
                leftTrackedAnimal,
                leftWriter));

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


