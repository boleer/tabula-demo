package org.tabula.demo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class DemoController implements Initializable {

    private DoubleProperty zoomLevelProperty = new SimpleDoubleProperty(1);
    private final ContextMenu cm = new ContextMenu();
    private Rectangle selectionBox;
    private Point2D selectionBoxStart;

    @FXML TextField fxZoomText;
    @FXML Slider fxSlider;
    @FXML Button fxZoomIn;
    @FXML Button fxZoomOut;
    @FXML Label label;
    @FXML Pane fxMapPane;
    @FXML Group fxMap;

    @FXML
    private void zoomIn() {
        zoomLevelProperty.set(zoomLevelProperty.get() + 0.1);
    }

    @FXML
    private void zoomOut() {
        zoomLevelProperty.set(zoomLevelProperty.get() - 0.1);
    }

    @Inject
    TestService service;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println(service.getTitle());
        preparePopMenu();
        prepareBindings();
        createCounters();
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

    private void createCounters() {

        List<String> imageNames = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("images/counters/images.properties"))))) {
            String imageName = in.readLine();
            while (imageName != null) {
                imageNames.add(imageName);
                imageName = in.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> counterPaths = new ArrayList<>();
        for (String imageName : imageNames) {
            if (imageName.matches(".*a.png")) {
                counterPaths.add(imageName.substring(0, imageName.length() - 5));
            }
        }

        double startX = 0;
        double startY = 200;
        int i = 0;
        for (String path : counterPaths) {
            CounterNode counter;
            if (getClass().getClassLoader().getResourceAsStream("images/counters/" + (path + "b.png")) == null) {
                counter = new CounterNode("/images/counters/" + path + "a.png",  null, cm);
            } else {
                counter = new CounterNode("/images/counters/" + path + "a.png",  "/images/counters/" + path + "b.png", cm);
            }
            i = i + 1;

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

    private void preparePopMenu() {
        MenuItem flipMenuItem = new MenuItem("Flip");
        flipMenuItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/rotate-right-icon.png"))));
        flipMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+F"));

        MenuItem rotateRightMenuItem = new MenuItem("Rotate right");
        rotateRightMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
        rotateRightMenuItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/flip-icon.png"))));
        MenuItem rotateLeftMenuItem = new MenuItem("Rotate left");
        rotateLeftMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+L"));
        rotateLeftMenuItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/rotate-left-icon.png"))));

        cm.getItems().add(flipMenuItem);
        cm.getItems().add(rotateRightMenuItem);
        cm.getItems().add(rotateLeftMenuItem);
    }

}