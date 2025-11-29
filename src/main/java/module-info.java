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

    // --- DATABASE / JPA (NEW) ---
    requires spring.data.jpa;        // Fixes "package ... not visible"
    requires jakarta.persistence;    // Fixes @Entity, @Id not found
    requires org.hibernate.orm.core; // Allows Hibernate to talk to DB
    requires spring.tx;              // Handles database transactions

    // --- Utils ---
    requires com.google.gson;
    requires static lombok;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;


    // --- EXPORTS ---
    exports com.example.cinematch;
    exports com.cinematch.cinematchbackend;
    exports com.cinematch.cinematchbackend.model;

    // --- OPENS ---

    // 1. OPEN MAIN APPS
    opens com.cinematch.cinematchbackend to spring.core, spring.beans, spring.context, spring.boot;
    opens com.example.cinematch to javafx.fxml, spring.core, spring.boot, com.google.gson, com.fasterxml.jackson.databind;

    // 2. OPEN CONTROLLERS
    opens com.cinematch.cinematchbackend.controller to spring.beans, spring.web, spring.context, spring.core;

    // 3. OPEN SERVICES
    opens com.cinematch.cinematchbackend.services to spring.beans, spring.context, spring.core;

    // 4. OPEN MODELS
    // Added 'org.hibernate.orm.core' so the DB engine can read your User class
    opens com.cinematch.cinematchbackend.model to spring.core, spring.beans, spring.context, com.google.gson, spring.web, org.hibernate.orm.core, com.fasterxml.jackson.databind;
    exports com.cinematch.cinematchbackend.repository;
    opens com.cinematch.cinematchbackend.repository to spring.beans, spring.boot, spring.context, spring.core, spring.data.jpa;

    // 5. OPEN REPOSITORIES (Uncommented & Updated)
    // Spring needs to see this to create the database connection
}