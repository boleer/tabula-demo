module simple.demo {

    requires javafx.controls;
    requires javafx.fxml;
    requires weld.se.shaded;
    requires java.desktop;
    requires java.logging;

    exports org.tabula.demo;
    exports org.tabula.demo.cdi;
    exports org.tabula.demo.eventing;

    opens org.tabula.demo to weld.se.shaded, javafx.fxml;
    opens images;
    opens images.counters;
    opens org.tabula.demo.cdi to javafx.fxml, weld.se.shaded;

}