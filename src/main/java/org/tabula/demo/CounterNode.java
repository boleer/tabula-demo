package org.tabula.demo;

import javafx.animation.RotateTransition;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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
    private Image frontImage;
    private Image backImage;
    private final ContextMenu contextMenu;

    public CounterNode(String frontImagePath, String backImagePath, final ContextMenu contextMenu) {
        frontImage = new Image(getClass().getResourceAsStream(frontImagePath));
        backImage = backImagePath != null ? new Image(getClass().getResourceAsStream(backImagePath)) : null;
        setUserData(frontImagePath);
        view = new ImageView(frontImage);
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
            node.getChildren().get(0).setEffect(ds);
            node.getChildren().get(0).setOpacity(0.5D);
            node.setLayoutX(node.getLayoutX() + (event.getX() - lastX));
            node.setLayoutY(node.getLayoutY() + (event.getY() - lastY));
            event.consume();
        });

        setOnMouseReleased(event -> {
            Pane node = ((Pane) event.getSource());
            node.getChildren().get(0).setOpacity(1.0D);
            node.getChildren().get(0).setEffect(null);
        });

        view.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                for (MenuItem menuItem : contextMenu.getItems()) {
                    if (menuItem.getText().equals("Rotate left")) {
                        menuItem.setOnAction(actionEvent -> rotateLeft());
                    }
                    if (menuItem.getText().equals("Rotate right")) {
                        menuItem.setOnAction(actionEvent -> rotateRight());
                    }
                    if (menuItem.getText().equals("Flip")) {
                        if (backImage == null) {
                            menuItem.disableProperty().set(true);
                        } else {
                            menuItem.disableProperty().set(false);
                            menuItem.setOnAction(actionEvent -> flip());
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
        if (view.getImage() == frontImage) {
            view.setImage(backImage);
        } else {
            view.setImage(frontImage);
        }
    }

}
