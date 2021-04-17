package org.tabula.demo;

import javafx.application.Application;
import javafx.stage.Stage;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.util.AnnotationLiteral;

import org.tabula.demo.cdi.StartupScene;

public class DemoApplication extends Application {

    private SeContainer container;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        container = initializer.initialize();
        container.getBeanManager().fireEvent(stage, new AnnotationLiteral<StartupScene>() {});
    }

    @Override
    public void stop() {
        container.close();
    }

}
