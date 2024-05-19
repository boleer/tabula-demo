package org.tabula.demo;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.tabula.demo.eventing.CounterMovedEvent;

@Singleton
public class DemoController implements Initializable {

    private final DoubleProperty zoomLevelProperty = new SimpleDoubleProperty(1);
    private Rectangle selectionBox;
    private Rectangle zoomBox;
    private Point2D selectionBoxStart;

    private final DoubleProperty mouseX = new SimpleDoubleProperty(0);
    private final DoubleProperty mouseY = new SimpleDoubleProperty(0);

    @FXML
    TextField fxZoomText;
    @FXML
    Slider fxSlider;
    @FXML
    Button fxZoomIn;
    @FXML
    Button fxZoomOut;
    @FXML
    Label label;
    @FXML
    StackPane fxMapPane;
    @FXML
    Group fxMap;
    @FXML
    Canvas gridCanvas;
    @FXML
    Label posLabel;
    @FXML
    ImageView mapImageView;
    @FXML
    Canvas zoomedMap;
    @FXML
    StackPane zoomedOutPane;
    @FXML
    ScrollPane scrollPane;

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

        final double mapHeight = mapImageView.getImage().getHeight();
        final double mapAspectRatio = mapImageView.getImage().getWidth() / mapImageView.getImage().getHeight();
        final double zoomScale = 200 / mapHeight;

        zoomedOutPane.setPrefSize(200 * mapAspectRatio, 200);
        zoomedMap.setHeight(200);
        zoomedMap.setWidth(200 * mapAspectRatio);

        GraphicsContext gcZoomed = zoomedMap.getGraphicsContext2D();
        WritableImage snapshot = fxMapPane.snapshot(null, null);
        gcZoomed.drawImage(snapshot, 0, 0, 200 * mapAspectRatio, 200);

        Platform.runLater(() -> {
            Bounds bounds = scrollPane.getViewportBounds();

            if (zoomBox == null) {
                zoomBox = new Rectangle(bounds.getWidth() * zoomScale, bounds.getHeight() * zoomScale);
                zoomBox.setFill(new Color(0.5, 0.5, 0.5, 0.2));
                zoomBox.setStrokeType(StrokeType.OUTSIDE);
                zoomBox.setStroke(Color.BLACK);
                zoomBox.setStrokeDashOffset(2);
                zoomBox.setStrokeWidth(1);
                zoomBox.setTranslateX(0);
                zoomBox.setTranslateY(0);
                zoomedOutPane.alignmentProperty().setValue(javafx.geometry.Pos.TOP_LEFT);
                zoomedOutPane.getChildren().add(zoomBox);
            }
        });

        scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            Bounds bounds = scrollPane.getViewportBounds();
            if (!oldValue.equals(newValue) && zoomedOutPane.getChildren().size() > 1) {
                zoomedOutPane.getChildren().remove(1);
                zoomBox = new Rectangle(bounds.getWidth() * zoomScale, bounds.getHeight() * zoomScale);
                zoomBox.setFill(new Color(0.5, 0.5, 0.5, 0.2));
                zoomBox.setStrokeType(StrokeType.OUTSIDE);
                zoomBox.setStroke(Color.BLACK);
                zoomBox.setStrokeDashOffset(2);
                zoomBox.setStrokeWidth(1);
                Pane pane = new Pane(zoomBox);
                zoomBox.setTranslateX(0);
                zoomBox.setTranslateY(0);
                zoomedOutPane.alignmentProperty().setValue(javafx.geometry.Pos.TOP_LEFT);
                zoomedOutPane.getChildren().add(zoomBox);

                double translateY = (zoomedOutPane.getHeight() - zoomBox.getHeight()) * scrollPane.getVvalue();
                zoomBox.setTranslateY(translateY <= 0 ? 0 : translateY);

                double translateX = (zoomedOutPane.getWidth() - zoomBox.getWidth()) * scrollPane.getHvalue();
                zoomBox.setTranslateX(translateX);

            }
        });

        scrollPane.vvalueProperty().addListener((s, oldvalue, newValue) -> {
            System.out.println(s);
            double translateY = (zoomedOutPane.getHeight() - zoomBox.getHeight()) * newValue.doubleValue();
            zoomBox.setTranslateY(translateY <= 0 ? 0 : translateY);
        });

        scrollPane.hvalueProperty().addListener((_, _, newValue) -> {
            double translateX = (zoomedOutPane.getWidth() - zoomBox.getWidth()) * newValue.doubleValue();
            zoomBox.setTranslateX(translateX);
        });

        // Bind label's text property to mouse position properties
        posLabel.textProperty().bind(
                mouseX.asString("X: %.0f")
                        .concat(", ")
                        .concat(mouseY.asString("Y: %.0f"))
        );

        fxMapPane.setOnMouseMoved(event -> {
            mouseX.set(event.getX());
            mouseY.set(event.getY());
        });

        fxMapPane.addEventHandler(CounterMovedEvent.COUNTER_MOVED_EVENT_TYPE, _ -> {
            WritableImage snap = fxMapPane.snapshot(null, null);
            gcZoomed.drawImage(snap, 0, 0, 200 * mapAspectRatio, 200);
        });
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
                selectionBox.relocate(selectionBoxStart.getX(), selectionBoxStart.getY());
                fxMap.getChildren().add(selectionBox);
            }
            selectionBox.setWidth(mouseEvent.getX() - selectionBoxStart.getX());
            selectionBox.setHeight(mouseEvent.getY() - selectionBoxStart.getY());
        });

        fxMapPane.setOnMouseReleased(_ -> {
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
            counter.relocate(startX, startY);
        }
        fxMap.getChildren().addAll(counters);
    }

    private void prepareBindings() {
        fxMapPane.scaleXProperty().bindBidirectional(zoomLevelProperty);
        fxMapPane.scaleYProperty().bindBidirectional(zoomLevelProperty);
        label.textProperty().bind(zoomLevelProperty.multiply(100).asString().concat(" %"));
        fxSlider.valueProperty().bindBidirectional(zoomLevelProperty);
        fxZoomText.textProperty().bindBidirectional(zoomLevelProperty, new DecimalFormat());
    }

}