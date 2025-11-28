package com.example.cinematch;

// Import your Backend Models
import com.cinematch.cinematchbackend.model.User;
// If your User class is in a different package, change the line above!

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class HelloApplication extends Application {

    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        // --- Header Section ---
        Label logoLabel = new Label("CineMatch");
        logoLabel.setStyle("-fx-text-fill: #E50914;");
        logoLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.color(0, 0, 0, 0.5));
        logoLabel.setEffect(shadow);

        Button homeBtn = new Button("Home Page");
        homeBtn.setOnAction(event -> showHomeView());
        homeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        makeButtonAnimated(homeBtn, false);

        Button top10Btn = new Button("Top 10");
        top10Btn.setOnAction(event -> showTop10View());
        top10Btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        makeButtonAnimated(top10Btn, false);

        Button loginBtn = new Button("Login / Register");
        loginBtn.setOnAction(event -> showLoginView());
        loginBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        makeButtonAnimated(loginBtn, true);

        Button quizBtn = new Button("Quiz");
        quizBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; ");
        makeButtonAnimated(quizBtn, false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(15);
        header.getChildren().addAll(logoLabel, spacer, homeBtn, top10Btn, quizBtn, loginBtn);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");

        root.setTop(header);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #141E30, #243B55);");

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("CineMatch App");
        primaryStage.setScene(scene);
        primaryStage.show();

        showHomeView();
    }

    private void showHomeView() {
        Label welcomeLabel = new Label("Welcome to CineMatch");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        Label subTitle = new Label("Search for your favourite movie or actor/actress");
        subTitle.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 18px;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefSize(300, 40);
        searchField.setStyle("-fx-background-color: white; -fx-font-size: 14px; -fx-background-radius: 20 0 0 20; -fx-padding: 0 15;");

        Button searchButton = new Button("Search");
        searchButton.setPrefSize(120, 40);
        searchButton.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 0 20 20 0;");
        searchButton.setOnAction(e -> performSearch(searchField.getText()));
        searchField.setOnAction(e -> performSearch(searchField.getText()));

        HBox searchBox = new HBox(0, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        VBox.setMargin(searchBox, new Insets(30, 0, 0, 0));

        VBox homeContent = new VBox(10, welcomeLabel, subTitle, searchBox);
        homeContent.setAlignment(Pos.CENTER);

        root.setCenter(homeContent);
    }

    private void showLoginView() {
        Label loginTitle = new Label("Sign In");
        loginTitle.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Email or Username");
        usernameField.setPrefHeight(40);
        usernameField.setStyle("-fx-background-radius: 5; -fx-background-color: #333; -fx-text-fill: white;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-background-radius: 5; -fx-background-color: #333; -fx-text-fill: white;");

        Button signInBtn = new Button("Sign In");
        signInBtn.setPrefWidth(300);
        signInBtn.setPrefHeight(40);
        signInBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");

        Label messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold;");

        signInBtn.setOnAction(event -> {
            signInBtn.setDisable(true);
            String username = usernameField.getText();
            String password = passwordField.getText();

            new Thread(() -> {
                try {
                    // Assuming User constructor: User(username, password)
                    User user = new User();
                    user.setUsername(username); // Setters are safer if you have them
                    user.setPassword(password);

                    String jsonBody = new Gson().toJson(user);

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/auth/login"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            messageLabel.setStyle("-fx-text-fill: lightgreen;");
                            messageLabel.setText("Login Successful!");
                            showHomeView();
                        } else {
                            messageLabel.setStyle("-fx-text-fill: red;");
                            messageLabel.setText("Login Failed: " + response.statusCode());
                        }
                        signInBtn.setDisable(false);
                    });

                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        messageLabel.setStyle("-fx-text-fill: red;");
                        messageLabel.setText("Connection Error: " + ex.getMessage());
                        signInBtn.setDisable(false);
                    });
                }
            }).start();
        });

        Label registerLink = new Label("New to CineMatch? Sign up now.");
        registerLink.setStyle("-fx-text-fill: #cccccc; -fx-cursor: hand;");
        registerLink.setOnMouseClicked(e -> showRegisterView());
        registerLink.setOnMouseEntered(e -> registerLink.setStyle("-fx-text-fill: white; -fx-underline: true;"));
        registerLink.setOnMouseExited(e -> registerLink.setStyle("-fx-text-fill: #cccccc; -fx-underline: false;"));

        VBox loginForm = new VBox(20, loginTitle, usernameField, passwordField, signInBtn, registerLink, messageLabel);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setPadding(new Insets(20));
        loginForm.setMaxWidth(400);
        loginForm.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-background-radius: 10;");

        root.setCenter(loginForm);
    }

    private void showRegisterView() {
        Label regTitle = new Label("Create Account");
        regTitle.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-background-radius: 5; -fx-background-color: #333; -fx-text-fill: white;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setPrefHeight(40);
        userField.setStyle("-fx-background-radius: 5; -fx-background-color: #333; -fx-text-fill: white;");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setPrefHeight(40);
        passField.setStyle("-fx-background-radius: 5; -fx-background-color: #333; -fx-text-fill: white;");

        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm Password");
        confirmPassField.setPrefHeight(40);
        confirmPassField.setStyle("-fx-background-radius: 5; -fx-background-color: #333; -fx-text-fill: white;");

        Button registerBtn = new Button("Sign Up");
        registerBtn.setPrefWidth(300);
        registerBtn.setPrefHeight(40);
        registerBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        makeButtonAnimated(registerBtn, true);

        Label messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold;");

        registerBtn.setOnAction(e -> {
            registerBtn.setDisable(true);
            String email = emailField.getText();
            String username = userField.getText();
            String password = passField.getText();

            if (!password.equals(confirmPassField.getText())) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Passwords do not match!");
                registerBtn.setDisable(false);
                return;
            }

            new Thread(() -> {
                try {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(username);
                    newUser.setPassword(password);

                    String jsonBody = new Gson().toJson(newUser);

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/auth/register"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            messageLabel.setStyle("-fx-text-fill: lightgreen;");
                            messageLabel.setText("Success! Redirecting...");
                            showLoginView();
                        } else {
                            messageLabel.setStyle("-fx-text-fill: red;");
                            messageLabel.setText("Error: " + response.body());
                        }
                        registerBtn.setDisable(false);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        messageLabel.setStyle("-fx-text-fill: red;");
                        messageLabel.setText("Connection Error");
                        registerBtn.setDisable(false);
                    });
                }
            }).start();
        });

        Label loginLink = new Label("Already have an account? Sign in.");
        loginLink.setStyle("-fx-text-fill: #cccccc; -fx-cursor: hand;");
        loginLink.setOnMouseClicked(e -> showLoginView());

        VBox regForm = new VBox(20, regTitle, emailField, userField, passField, confirmPassField, registerBtn, loginLink, messageLabel);
        regForm.setAlignment(Pos.CENTER);
        regForm.setPadding(new Insets(40));
        regForm.setMaxWidth(400);
        regForm.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-background-radius: 10;");

        root.setCenter(regForm);
    }

    // --- Helper Methods ---
    private void showTop10View() {
        // Implement logic to fetch top 10 movies
    }

    private void performSearch(String query) {
        // Implement logic to search movies
    }

    private void makeButtonAnimated(Button btn, boolean isRedButton) {
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.10);
            btn.setScaleY(1.10);
            if (isRedButton) {
                double radius = btn.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius();
                btn.setStyle("-fx-background-color: #ff1f2c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: " + radius + ";");
            }
        });
        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            if (isRedButton) {
                double radius = btn.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius();
                btn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: " + radius + ";");
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

