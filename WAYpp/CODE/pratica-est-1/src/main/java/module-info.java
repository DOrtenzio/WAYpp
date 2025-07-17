module praticaest1.praticaest1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires io.github.cdimascio.dotenv.java;
    requires com.gluonhq.maps;

    opens praticaest1.praticaest1 to javafx.fxml, com.fasterxml.jackson.databind;
    opens praticaest1.praticaest1.obj to com.fasterxml.jackson.databind;
    opens praticaest1.praticaest1.utility to com.fasterxml.jackson.databind;

    exports praticaest1.praticaest1;
    exports praticaest1.praticaest1.obj;
    exports praticaest1.praticaest1.utility;

}