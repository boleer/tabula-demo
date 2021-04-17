package org.tabula.demo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tabula.demo.cdi.StartupScene;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.io.IOException;

public class AppInitializationListener {

    @Inject
    private FXMLLoader fxmlLoader;

    public void start(@Observes @StartupScene Stage stage) {
        try {
            Parent group = fxmlLoader.load(getClass().getResourceAsStream("/fxml/simple-demo.fxml"));
            Scene scene = new Scene(group);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
