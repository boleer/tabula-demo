package org.tabula.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DemoApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent group = FXMLLoader.load(getClass().getResource("/fxml/simple-demo.fxml"));
        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
