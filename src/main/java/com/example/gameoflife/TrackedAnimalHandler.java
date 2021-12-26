package com.example.gameoflife;

import backend.Animal;
import backend.Map;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

@SuppressWarnings("ClassEscapesDefinedScope")
public class TrackedAnimalHandler {
    private final Map map;
    private final Label info;
    public TrackedAnimalHandler(Map map) {
        this.map = map;
        this.info = new Label();
    }

    public VBox createLabel() {
        return new VBox(info);
    }

    public void refreshLabel(Animal animal) {
        Platform.runLater(() -> {
            info.setText("Genes: "+animal.getGenes().toString());
            if(animal.isDead()){
                info.setText(
                        "Died in the epoch " +
                        map.getEpochNumber() +
                        " Had  children " +
                        animal.getChildrenAmount() +
                        " Genes: "+
                        animal.getGenes().toString());
            }
        });
    }
}
