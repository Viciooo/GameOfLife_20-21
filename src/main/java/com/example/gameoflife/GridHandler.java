package com.example.gameoflife;

import backend.Map;
import backend.Vector2d;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class GridHandler {
    private GridPane gridPane = new GridPane();
    private final Map map;
    private final int x;
    private final int y;

    public GridPane getGridPane() {
        return gridPane;
    }

    public GridHandler(Map map) {
        this.map = map;
        this.x = map.getWidth() + 2;
        this.y = map.getHeight() + 2;
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
        refreshMap();
    }

    private void clearGrid(){
        gridPane.setGridLinesVisible(false);
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        gridPane.getChildren().clear();
        gridPane.setGridLinesVisible(true);
    }

    public void refreshMap(){
        clearGrid();
        for (int i = 0; i < y; i++)
            for (int j = 0; j < x; j++) {
                int x1 = i;

                String text = "";
                if (i == 0 && j == 0) text = "y/x";
                else if (i == 0) text = String.valueOf(j - 1);
                else if (j == 0) text = String.valueOf(y - i - 1);
                else {
                    if (map.objectAt(new Vector2d(j - 1, i - 1)) != null) {
                        text = map.objectAt(new Vector2d(j - 1, i - 1)).toString();
                    }
                    x1 = y - i;
                }
                Label newLabel = new Label(text);
                GridPane.setConstraints(newLabel, j, x1);
                GridPane.setHalignment(newLabel, HPos.CENTER);
                gridPane.add(newLabel, j, x1);
            }
    }

}
