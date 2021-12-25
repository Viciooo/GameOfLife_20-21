package com.example.gameoflife;

import backend.Animal;
import backend.Grass;
import backend.Map;
import backend.Vector2d;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GridHandler {
    private final GridPane gridPane = new GridPane();
    private final Map map;
    private final int x;
    private final int y;
    private final int CELL_SIZE;
    private Animal trackedAnimal;
    FXMLLoader loader = new FXMLLoader();

    public synchronized Animal getTrackedAnimal() {
        return trackedAnimal;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public GridHandler(Map map) {
        this.map = map;
        this.x = map.getWidth();
        this.y = map.getHeight();
        refreshMap();
        this.CELL_SIZE = 40;
    }

    private void clearGrid() {
        gridPane.getChildren().clear();

        if(trackedAnimal != null && trackedAnimal.isDead()){
            this.trackedAnimal = null;
        }
    }

    public void refreshMap() {
        Platform.runLater(() -> {
            clearGrid();

            for (int i = 0; i <= y; i++) {
                for (int j = 0; j <= x; j++) {
                    Vector2d renderedPosition = new Vector2d(i, j);
                    if (map.objectAt(renderedPosition) instanceof Grass || map.objectAt(renderedPosition) instanceof Animal) {
                        try {
                            ImageView cell = new ImageView(new Image(new FileInputStream(map.objectAt(renderedPosition).toString())));
                            cell.setFitWidth(CELL_SIZE);
                            cell.setFitHeight(CELL_SIZE);
                            if(map.objectAt(renderedPosition) instanceof Animal){
                                Button cellButton = new Button();
                                cellButton.setPadding(new Insets(0, 0, 0, 0));
                                cellButton.setMaxWidth(CELL_SIZE);
                                cellButton.setMaxHeight(CELL_SIZE);
                                cellButton.setOnAction(event -> {
                                    ((Animal) map.objectAt(renderedPosition)).swapIsTracked();
                                    if(((Animal) map.objectAt(renderedPosition)).isTracked()){
                                        if(trackedAnimal != null){
                                            trackedAnimal.swapIsTracked();
                                        }
                                        this.trackedAnimal = (Animal) map.objectAt(renderedPosition);
                                    }else{
                                        this.trackedAnimal = null;
                                    }
                                });
                                if(((Animal) map.objectAt(renderedPosition)).isTracked()){
                                    ImageView trackedCell = new ImageView(new Image(new FileInputStream("src/main/resources/hedgehog_img_tracked.jpg")));
                                    trackedCell.setFitWidth(CELL_SIZE);
                                    trackedCell.setFitHeight(CELL_SIZE);
                                    cellButton.setGraphic(trackedCell);
                                }else{
                                    cellButton.setGraphic(cell);
                                }
                                gridPane.add(cellButton, i, j);
                            }else{
                                gridPane.add(cell, i, j);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Label newLabel = new Label();
                        newLabel.setMinWidth(CELL_SIZE);
                        newLabel.setMinHeight(CELL_SIZE);
                        if (map.isInJungle(renderedPosition)) {
                            newLabel.setText(renderedPosition.toString());
                            newLabel.setStyle("-fx-background-color: #177320;");
                        } else {
                            newLabel.setText(renderedPosition.toString());
                            newLabel.setStyle("-fx-background-color: #6bbf73;");
                        }
                        gridPane.add(newLabel, i, j);
                    }
                }
            }
        });
    }
}

