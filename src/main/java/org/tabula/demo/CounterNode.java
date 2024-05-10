package org.tabula.demo;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Objects;

/**
 * Pane node representing a counter.
 * <p>
 * The node implementation contains basic functionally for dragging, flipping and rotation.
 */
public class CounterNode extends Pane {

    private double lastX;
    private double lastY;

    private static final DropShadow ds = new DropShadow(10, 3.0f, 3.0f, Color.color(0.4f, 0.4f, 0.4f));

    private final ImageView view;
    private final Image frontImage;
    private final Image backImage;
    private final ContextMenu contextMenu;

    public CounterNode(String frontImagePath, String backImagePath, final ContextMenu contextMenu) {
        frontImage = new Image(getClass().getResourceAsStream(frontImagePath));
        backImage = backImagePath != null ? new Image(Objects.requireNonNull(getClass().getResourceAsStream(backImagePath))) : null;
        setUserData(frontImagePath);
        view = new ImageView(frontImage);
        view.setFitHeight(frontImage.getHeight());
        view.setFitWidth(frontImage.getWidth());
        this.contextMenu = contextMenu;
        getChildren().add(view);
        init();
    }


    private void init() {
        setOnMousePressed(event -> {
            lastX = event.getX();
            lastY = event.getY();
            toFront();
            event.consume();
        });

        setOnMouseDragged(event -> {
            Pane node = ((Pane) event.getSource());
            node.getChildren().getFirst().setEffect(ds);
            node.getChildren().getFirst().setOpacity(0.5D);
            node.setLayoutX(node.getLayoutX() + (event.getX() - lastX));
            node.setLayoutY(node.getLayoutY() + (event.getY() - lastY));
            event.consume();
        });

        setOnMouseReleased(event -> {
            Pane node = ((Pane) event.getSource());
            node.getChildren().getFirst().setOpacity(1.0D);
            node.getChildren().getFirst().setEffect(null);
        });

        view.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                for (MenuItem menuItem : contextMenu.getItems()) {
                    if (menuItem.getText().equals("Rotate left")) {
                        menuItem.setOnAction(_ -> rotateLeft());
                    }
                    if (menuItem.getText().equals("Rotate right")) {
                        menuItem.setOnAction(_ -> rotateRight());
                    }
                    if (menuItem.getText().equals("Flip")) {
                        if (backImage == null) {
                            menuItem.disableProperty().set(true);
                        } else {
                            menuItem.disableProperty().set(false);
                            menuItem.setOnAction(_ -> flip());
                        }
                    }
                }
                contextMenu.show(view, e.getScreenX(), e.getScreenY());
            }
        });

    }

    private void rotateRight() {
        RotateTransition transition = new RotateTransition(Duration.seconds(0.2), view);
        transition.setByAngle(90);
        transition.play();
    }

    private void rotateLeft() {
        RotateTransition transition = new RotateTransition(Duration.seconds(0.2), view);
        transition.setByAngle(-90);
        transition.play();
    }

    private void flip() {
        final double width = view.getImage().getWidth();
        final double height = view.getImage().getHeight();

        // Create perspective transform
        PerspectiveTransform perspectiveTransform = new PerspectiveTransform();
        perspectiveTransform.setUlx(0.0);
        perspectiveTransform.setUly(0.0);
        perspectiveTransform.setUrx(width);
        perspectiveTransform.setUry(0.0);
        perspectiveTransform.setLrx(width);
        perspectiveTransform.setLry(height);
        perspectiveTransform.setLlx(0.0);
        perspectiveTransform.setLly(height);

        // Set transform on counter node
        this.setEffect(perspectiveTransform);

        // Animate flip effect
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(perspectiveTransform.ulxProperty(), 0.0),
                new KeyValue(perspectiveTransform.uryProperty(), 0.0),
                new KeyValue(perspectiveTransform.urxProperty(), width),
                new KeyValue(perspectiveTransform.uryProperty(), 0.0),
                new KeyValue(perspectiveTransform.lrxProperty(), width),
                new KeyValue(perspectiveTransform.lryProperty(), height),
                new KeyValue(perspectiveTransform.llxProperty(), 0.0),
                new KeyValue(perspectiveTransform.llyProperty(), height)),
            new KeyFrame(Duration.millis(200), new KeyValue(perspectiveTransform.ulxProperty(), width),
                new KeyValue(perspectiveTransform.uryProperty(), 0.0),
                new KeyValue(perspectiveTransform.urxProperty(), 0.0),
                new KeyValue(perspectiveTransform.uryProperty(), 0.0),
                new KeyValue(perspectiveTransform.lrxProperty(), 0.0),
                new KeyValue(perspectiveTransform.lryProperty(), height),
                new KeyValue(perspectiveTransform.llxProperty(), width),
                new KeyValue(perspectiveTransform.llyProperty(), height)));

        // Update counter image after flip animation
        timeline.setOnFinished(event -> {
            if (view.getImage() == frontImage) {
                view.setImage(backImage);
            } else {
                view.setImage(frontImage);
            }
            this.setEffect(null); // Remove perspective transform after flip animation
        });

        timeline.play();
    }

}
