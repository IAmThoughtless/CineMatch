module com.example.cinematch {
    // --- Java Modules ---
    requires java.net.http;
    requires java.sql;

    // --- JavaFX ---
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;

    // --- 3rd Party UI ---
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // --- Spring Boot ---
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires spring.web;

    // --- Utils ---
    requires com.google.gson;
    requires static lombok;

    // --- EXPORTS ---
    exports com.example.cinematch;
    exports com.cinematch.cinematchbackend;

    // EXPORT MODEL so Gson/Jackson can see the class
    exports com.cinematch.cinematchbackend.model;

    // --- OPENS ---

    // 1. OPEN MAIN APPS
    opens com.cinematch.cinematchbackend to spring.core, spring.beans, spring.context, spring.boot;
    opens com.example.cinematch to javafx.fxml, spring.core, spring.boot, com.google.gson;

    // 2. OPEN CONTROLLERS
    opens com.cinematch.cinematchbackend.controller to spring.beans, spring.web, spring.context, spring.core;

    // 3. OPEN MODELS (Fixes Login "Bad Request" / Empty JSON)
    opens com.cinematch.cinematchbackend.model to spring.core, spring.beans, spring.context, com.google.gson, spring.web;

    // 4. OPTIONAL: Only uncomment these if the packages are NOT empty!
    // opens com.cinematch.cinematchbackend.service to spring.beans, spring.context, spring.core;
    // opens com.cinematch.cinematchbackend.repository to spring.beans, spring.context, spring.core;
}