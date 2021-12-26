package com.example.gameoflife;

import backend.Map;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GenomeDominantHandler {
    Map map;
    Label genomeDominant;
    public GenomeDominantHandler(Map map) {
        this.map = map;
        genomeDominant = new Label();
    }

    public VBox createLabel(){
        return new VBox(genomeDominant);
    }

    public void refreshLabel(){
        Platform.runLater(() -> {
            genomeDominant.setText("Most popular genome: "+map.getGenomeDominant().toString());
        });
    }
}
