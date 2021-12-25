package com.example.gameoflife;

import backend.Animal;
import backend.Map;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

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
//        Platform.runLater(() -> {
            if(animal.isDead()){
                info.setText("Died in the epoch " + map.getEpochNumber() + "\n Had "+animal.getChildrenAmount()+" children");
            }
//        });
    }
}
