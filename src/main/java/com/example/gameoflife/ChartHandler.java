package com.example.gameoflife;

import backend.Map;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

@SuppressWarnings("ClassEscapesDefinedScope")
public class ChartHandler {
    Map map;
    LineChart<Number, Number> lineChart;
    XYChart.Series<Number, Number> animalSeries;
    XYChart.Series<Number, Number> grassSeries;
    XYChart.Series<Number, Number> energySeries;
    XYChart.Series<Number, Number> lifeSpanSeries;
    XYChart.Series<Number, Number> childrenAmountSeries;
    final int WINDOW_SIZE;

    public ChartHandler(Map map) {
        WINDOW_SIZE = 100;
        this.map = map;
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Number of epoch");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Population");

        lineChart = new LineChart<>(xAxis, yAxis);

        animalSeries = new XYChart.Series<>();
        animalSeries.setName("Hedgehog population");

        grassSeries = new XYChart.Series<>();
        grassSeries.setName("Apples population");

        energySeries = new XYChart.Series<>();
        energySeries.setName("Average animal energy");

        lifeSpanSeries = new XYChart.Series<>();
        lifeSpanSeries.setName("Average animal lifespan");

        childrenAmountSeries = new XYChart.Series<>();
        childrenAmountSeries.setName("Average animal children amount");
    }

    public VBox createChart() {
        lineChart.getData().add(animalSeries);
        lineChart.getData().add(grassSeries);
        lineChart.getData().add(energySeries);
        lineChart.getData().add(lifeSpanSeries);
        lineChart.getData().add(childrenAmountSeries);
        lineChart.setCreateSymbols(false);
        return new VBox(lineChart);
    }

    public void refreshChart() {
        Platform.runLater(() -> {

            animalSeries.getData().add(new XYChart.Data<>(map.getEpochNumber(), map.getAnimalsAmount()));
            grassSeries.getData().add(new XYChart.Data<>(map.getEpochNumber(), map.getPlantsAmount()));
            energySeries.getData().add(new XYChart.Data<>(map.getEpochNumber(), map.getAvgAnimalsEnergy()));
            lifeSpanSeries.getData().add(new XYChart.Data<>(map.getEpochNumber(), map.getAvgAnimalLifeSpan()));
            childrenAmountSeries.getData().add(new XYChart.Data<>(map.getEpochNumber(), map.getAvgAnimalChildrenAmount()));
        });
    }
}