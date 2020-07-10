module simple.demo {

    requires javafx.controls;
    requires javafx.fxml;
    requires weld.se.shaded;
    requires java.desktop;

    exports org.tabula.demo;

    opens org.tabula.demo.cdi to weld.se.shaded;
    opens org.tabula.demo to weld.se.shaded, javafx.fxml;

    opens images;
    opens images.counters;

}