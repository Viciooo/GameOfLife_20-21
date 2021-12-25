package com.example.gameoflife;

import backend.Animal;
import backend.Grass;
import backend.Map;
import backend.Vector2d;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
    FXMLLoader loader = new FXMLLoader();


    public GridPane getGridPane() {
        return gridPane;
    }

    public GridHandler(Map map) {
        this.map = map;
        this.x = map.getWidth() + 1;
        this.y = map.getHeight() + 1;
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setGridLinesVisible(true);
        refreshMap();
    }

    private void clearGrid() {
        gridPane.setGridLinesVisible(false);
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        gridPane.getChildren().clear();
    }

    public void refreshMap() {
        Platform.runLater(() -> {
            clearGrid();
            for (int i = 0; i < x; i++) {
                ColumnConstraints columnConstraints = new ColumnConstraints(65);
                gridPane.getColumnConstraints().add(columnConstraints);
            }

            for (int i = 0; i < y; i++) {
                RowConstraints rowConstraints = new RowConstraints(65);
                gridPane.getRowConstraints().add(rowConstraints);
            }

            for (int i = 0; i < y; i++) {
                for (int j = 0; j < x; j++) {
                    Vector2d renderedPosition = new Vector2d(i, j);
                    if (map.objectAt(renderedPosition) instanceof Grass || map.objectAt(renderedPosition) instanceof Animal) {
                        try {
                            ImageView cell = new ImageView(new Image(new FileInputStream(map.objectAt(renderedPosition).toString())));
                            cell.setFitWidth(60);
                            cell.setFitHeight(60);
                            gridPane.add(cell, i, j);
                            GridPane.setConstraints(cell, i, j);
                            if (i == 0) {
                                System.out.println(renderedPosition);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Label newLabel = new Label();
                        newLabel.setMinWidth(60);
                        newLabel.setMinHeight(60);
                        if (map.isInJungle(renderedPosition)) {
                            newLabel.setStyle("-fx-background-color: #177320;");
                        } else {
                            newLabel.setStyle("-fx-background-color: #6bbf73;");
                        }
                        GridPane.setHalignment(newLabel, HPos.CENTER);
                        gridPane.add(newLabel, i, j);
                        GridPane.setConstraints(newLabel, i, j);
                    }
                }
                gridPane.setGridLinesVisible(true);
            }

        });
    }

}

