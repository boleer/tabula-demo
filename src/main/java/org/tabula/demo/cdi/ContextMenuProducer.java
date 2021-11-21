package org.tabula.demo.cdi;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ContextMenuProducer {

    @Produces
    public ContextMenu produceContextMenu() {
        MenuItem flipMenuItem = new MenuItem("Flip");
        flipMenuItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/rotate-right-icon.png"))));
        flipMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+F"));

        MenuItem rotateRightMenuItem = new MenuItem("Rotate right");
        rotateRightMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
        rotateRightMenuItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/flip-icon.png"))));

        MenuItem rotateLeftMenuItem = new MenuItem("Rotate left");
        rotateLeftMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+L"));
        rotateLeftMenuItem.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/rotate-left-icon.png"))));

        return new ContextMenu(flipMenuItem, rotateRightMenuItem, rotateLeftMenuItem);
    }

}
