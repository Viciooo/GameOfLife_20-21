module com.example.gameoflife {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;

    opens com.example.gameoflife to javafx.fxml;
    exports com.example.gameoflife;
}