package org.tabula.demo;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

@Singleton
public class DemoController implements Initializable {

    private final DoubleProperty zoomLevelProperty = new SimpleDoubleProperty(1);
    public Canvas fxEffectsCanvas;
    private Rectangle selectionBox;
    private Point2D selectionBoxStart;

    @FXML TextField fxZoomText;
    @FXML Slider fxSlider;
    @FXML Button fxZoomIn;
    @FXML Button fxZoomOut;
    @FXML Label label;
    @FXML StackPane fxMapPane;
    @FXML Group fxMap;

    @Inject
    private TestService service;

    @Inject
    private List<CounterNode> counters;

    @FXML
    private void zoomIn() {
        zoomLevelProperty.set(zoomLevelProperty.get() + 0.1);
    }

    @FXML
    private void zoomOut() {
        zoomLevelProperty.set(zoomLevelProperty.get() - 0.1);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(service.getTitle());
        prepareBindings();
        addCounters();
        prepareSelectionBoxListeners();
    }

    private void prepareSelectionBoxListeners() {
        fxMapPane.setOnMousePressed(mouseEvent -> selectionBoxStart = new Point2D(mouseEvent.getX(), mouseEvent.getY()));

        fxMapPane.setOnMouseDragged(mouseEvent -> {
            if (selectionBox == null) {
                selectionBox = new Rectangle();
                selectionBox.setFill(new Color(0.5, 0.5, 0.5, 0.3));
                selectionBox.setStrokeType(StrokeType.OUTSIDE);
                selectionBox.setStroke(Color.BLACK);
                selectionBox.setStrokeDashOffset(2);
                selectionBox.setStrokeWidth(1);
                selectionBox.setLayoutX(selectionBoxStart.getX());
                selectionBox.setLayoutY(selectionBoxStart.getY());
                fxMap.getChildren().add(selectionBox);
            }
            selectionBox.setWidth(mouseEvent.getX() - selectionBoxStart.getX());
            selectionBox.setHeight(mouseEvent.getY() - selectionBoxStart.getY());
        });

        fxMapPane.setOnMouseReleased(mouseEvent -> {
            fxMap.getChildren().remove(selectionBox);
            selectionBox = null;
        });

    }

    private void addCounters() {
            double startX = 0;
            double startY = 200;
            int i = 0;

            for (CounterNode counter : counters) {
            i += 1;
            if (i % 25 == 0) {
                startY += 50;
                startX = 0;
            }

            startX += 50;
            counter.setLayoutX(startX);
            counter.setLayoutY(startY);
            fxMap.getChildren().add(counter);
        }
    }

    private void prepareBindings() {
        fxMapPane.scaleXProperty().bindBidirectional(zoomLevelProperty);
        fxMapPane.scaleYProperty().bindBidirectional(zoomLevelProperty);
        label.textProperty().bind(zoomLevelProperty.multiply(100).asString().concat(" %"));
        fxSlider.valueProperty().bindBidirectional(zoomLevelProperty);
        fxZoomText.textProperty().bindBidirectional(zoomLevelProperty, new DecimalFormat());
    }

}