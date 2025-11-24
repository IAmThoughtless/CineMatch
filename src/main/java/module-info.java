module com.example.cinematch {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires java.desktop;
    requires com.google.gson;

    requires java.net.http;
    requires java.desktop;
    requires com.google.gson;

    exports com.example.cinematch;
    opens com.example.cinematch to javafx.fxml, com.google.gson;

}