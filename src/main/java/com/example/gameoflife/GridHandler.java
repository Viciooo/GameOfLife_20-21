package com.example.gameoflife;

import backend.Map;
import backend.Vector2d;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

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
        this.x = map.getWidth() + 2;
        this.y = map.getHeight() + 2;
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setGridLinesVisible(true);
        refreshMap();
    }

    private void clearGrid(){
        gridPane.setGridLinesVisible(false);
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        gridPane.getChildren().clear();
    }

    public void refreshMap(){
        Platform.runLater(() -> {
            clearGrid();
            gridPane.setPadding(new Insets(5, 5, 5, 5));
            gridPane.setGridLinesVisible(true);

            for (int i = 0; i < x; i++) {
                ColumnConstraints columnConstraints = new ColumnConstraints(100);
                columnConstraints.setPercentWidth(100.0 / x);
                gridPane.getColumnConstraints().add(columnConstraints);
            }

            for (int i = 0; i < y; i++) {
                RowConstraints rowConstraints = new RowConstraints(100);
                rowConstraints.setPercentHeight(100.0 / y);
                gridPane.getRowConstraints().add(rowConstraints);
            }
            gridPane.setMinWidth(10 * x);
            gridPane.setMinHeight(10 * y);
            for (int i = 0; i < y; i++)
                for (int j = 0; j < x; j++) {
                    int x1 = i;
                    Vector2d currPosition = new Vector2d(j - 1, i - 1);
                    String text = "";
                    if (i == 0 && j == 0) text = "y/x";
                    else if (i == 0) text = String.valueOf(j - 1);
                    else if (j == 0) text = String.valueOf(y - i - 1);
                    else {
                        if (map.objectAt(currPosition) != null) {
                            text = map.objectAt(currPosition).toString();
                        }
                        x1 = y - i;
                    }
                    Label newLabel = new Label(text);
                    if(!Objects.equals(text, "")){
                        try {
                            ImageView cell = new ImageView(new Image(new FileInputStream(text)));
                            GridPane.setConstraints(cell, j, x1);
                            gridPane.add(cell, j, x1);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else{
                        if(map.isInJungle(currPosition)){
                            newLabel.setTextFill(Color.DARKGREEN);
                        }else {
                            newLabel.setTextFill(Color.LIGHTGREEN);
                        }
                        GridPane.setConstraints(newLabel, j, x1);
                        GridPane.setHalignment(newLabel, HPos.CENTER);
                        gridPane.add(newLabel, j, x1);
                    }

                }
            gridPane.setGridLinesVisible(true);
        });
    }

}
