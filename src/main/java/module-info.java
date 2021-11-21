module simple.demo {

    requires javafx.controls;
    requires javafx.fxml;
    requires weld.se.shaded;
    requires java.desktop;

    exports org.tabula.demo;
    exports org.tabula.demo.cdi;

    opens org.tabula.demo to weld.se.shaded, javafx.fxml;
    opens images;
    opens images.counters;
    opens org.tabula.demo.cdi to javafx.fxml, weld.se.shaded;

}