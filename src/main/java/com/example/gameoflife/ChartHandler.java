package com.example.gameoflife;

import backend.Map;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

public class ChartHandler {
    Map map;
    LineChart lineChart;
    XYChart.Series dataSeries;

    public ChartHandler(Map map) {
        this.map = map;
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Number of epoch");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Population");

        lineChart = new LineChart(xAxis, yAxis);

        dataSeries = new XYChart.Series();
        dataSeries.setName(map.toString());
    }

    public VBox createChart(){
        dataSeries.getData().add(new XYChart.Data(0, 0));
        lineChart.getData().add(dataSeries);
        return new VBox(lineChart);
    }
    public void refreshChart(){
        dataSeries.getData().add(map.getEpochNumber(),map.getAnimalsAmount());
    }
}
