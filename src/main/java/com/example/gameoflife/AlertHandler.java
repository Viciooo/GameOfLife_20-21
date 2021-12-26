package com.example.gameoflife;

import backend.Map;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

@SuppressWarnings("ClassEscapesDefinedScope")
public class AlertHandler {
    private final Map map;
    private final Alert alert = new Alert(AlertType.INFORMATION);

    public AlertHandler(Map map) {
        this.map = map;
    }

    public void handle(){
        alert.setTitle(3-map.getMagicTricksLeft() + " Magic alert");
        alert.setContentText("Magic happened, magic tricks left " + map.getMagicTricksLeft());
        Platform.runLater(alert::show);
    }
}
