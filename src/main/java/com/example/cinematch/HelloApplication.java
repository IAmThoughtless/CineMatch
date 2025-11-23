package com.example.cinematch;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class HelloApplication extends Application {


    private BorderPane root;

    public void start(Stage primaryStage) {

        root=new BorderPane();

        Label logoLabel = new Label("CineMatch");
        logoLabel.setStyle("-fx-text-fill: #E50914;");
        logoLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.color(0, 0, 0, 0.5));
        logoLabel.setEffect(shadow);

        Button homeBtn = new Button("Home Page");
        homeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");


        Button top10Btn = new Button("Top 10");
        top10Btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");


        Button loginBtn = new Button("Login / Register");
        loginBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");


        Button QuizBtn = new Button("Quiz");
        QuizBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; ");


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(15);
        header.getChildren().addAll(logoLabel, spacer, homeBtn, top10Btn, QuizBtn, loginBtn);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");

        root.setTop(header);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #141E30, #243B55);");



        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("CineMatch App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

