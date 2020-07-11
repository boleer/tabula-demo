package org.tabula.demo;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class CounterNodeTest extends ApplicationTest {

    @BeforeEach
    @Override
    public void init() throws Exception {
        ApplicationTest.launch(DemoApplication.class);
    }

    @Override
    public void start(Stage stage) {
        stage.show();
    }

    @Test
    @DisplayName("that counter nodes can be instantiated")
    void testCounterNode() {
        CounterNode node = new CounterNode("/images/counters/D8_counters_1_00_00_a.png", "/images/counters/D8_counters_1_00_00_b.png",
                new ContextMenu());
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("that click on zoomIn button zooms in 10 percent")
    void zoomIn() {
        clickOn("#fxZoomIn");
        assertThat(lookup("#fxZoomText").queryAs(TextField.class).textProperty().getValue()).isIn("1,1", "1.1");
        assertThat(lookup("#fxMapPane").queryAs(Pane.class).scaleXProperty().getValue()).isEqualTo(1.1);
    }

    @Test
    @DisplayName("that click on zoomOut button zooms out 10 percent")
    void zoomOut() {
        clickOn("#fxZoomOut");
        assertThat(lookup("#fxZoomText").queryAs(TextField.class).textProperty().getValue()).isIn("0,9", "0.9");
        assertThat(lookup("#fxMapPane").queryAs(Pane.class).scaleXProperty().getValue()).isEqualTo(0.9);
    }

}
