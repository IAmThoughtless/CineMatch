package com.example.cinematch;

// Import your Backend Models
// If your User class is in a different package, change the line above!

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HelloApplication extends Application {

    private BorderPane root;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private final int TOTAL_QUESTIONS = 5;
    private java.util.List<com.cinematch.cinematchbackend.model.QuizQuestion> loadedQuestions;

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
        root.setTop(createHeader());
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

    private void showTop10View() {
        Label loadingLabel = new Label("Fetching Top 10 Movies...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
        ProgressIndicator indicator = new ProgressIndicator();
        VBox loadingBox = new VBox(20, loadingLabel, indicator);
        loadingBox.setAlignment(Pos.CENTER);
        root.setCenter(loadingBox);

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/top-10"))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Platform.runLater(() -> {
                    Gson gson = new Gson();
                    MovieResponse movies = gson.fromJson(response.body(), MovieResponse.class);

                    if (response.statusCode() != 200 || movies == null || movies.results == null) {
                        Label errorLabel = new Label("Could not fetch top movies. Check API key and network connection.");
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                        VBox errorBox = new VBox(errorLabel);
                        errorBox.setAlignment(Pos.CENTER);
                        root.setCenter(errorBox);
                    }
                    else {
                        VBox top10Content = buildMovieListUI("⭐ Top 10 Popular Movies ⭐", movies);
                        root.setCenter(top10Content);
                    }

                });

            } catch (Exception ex) {
                Label errorLabel = new Label("Could not fetch top movies. Check API key and network connection.");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                VBox errorBox = new VBox(errorLabel);
                errorBox.setAlignment(Pos.CENTER);
                root.setCenter(errorBox);
            }
        }).start();
    }
    private void showLoginView() {
        Label loginTitle = new Label("Sign In");
        loginTitle.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
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
                    User user = new User(username, password);

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
                            UserSession.getInstance().setUsername(username);
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
                    User newUser = new User(email, username, password);
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
    private VBox buildMovieListUI(String headerText, MovieResponse movieResponse) {

        Label titleLabel = new Label(headerText);
        titleLabel.setStyle("-fx-text-fill: #E50914; -fx-font-size: 32px; -fx-font-weight: bold;");

        VBox movieListView = new VBox(15);
        movieListView.setPadding(new Insets(30));
        movieListView.setAlignment(Pos.TOP_CENTER);
        movieListView.setMaxWidth(800);


        int limit = Math.min(20, movieResponse.results.size());

        for (int i = 0; i < limit; i++) {
            Movie m = movieResponse.results.get(i);

            ImageView posterView = createPosterImageView(m.poster_path);

            Label movieTitle = new Label(m.title);
            movieTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

            Label movieDetails = new Label(
                    String.format("Rating: %.1f/10 (%d votes) | Release: %s",
                            m.vote_average, m.vote_count, (m.release_date != null ? m.release_date : "N/A")));
            movieDetails.setStyle("-fx-text-fill: #aaa; -fx-font-size: 14px;");

            String overviewText = m.overview != null ? m.overview : "No description available.";
            Label overview = new Label(
                    (overviewText.length() > 140 ? overviewText.substring(0, 140) + "..." : overviewText));
            overview.setWrapText(true);
            overview.setStyle("-fx-text-fill: #ccc;");

            VBox textContent = new VBox(5, movieTitle, movieDetails, overview);

            HBox movieCard = new HBox(20.0, posterView, textContent);
            movieCard.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(textContent, Priority.ALWAYS);

            movieListView.getChildren().add(movieCard);

            if (i < limit - 1) {
                Region separator = new Region();
                separator.setPrefHeight(1);
                separator.setStyle("-fx-background-color: #333;");
                movieListView.getChildren().add(separator);
            }
        }

        ScrollPane scrollPane = new ScrollPane(movieListView);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox finalLayout = new VBox(20, titleLabel, scrollPane);
        finalLayout.setPadding(new Insets(30));
        finalLayout.setAlignment(Pos.TOP_CENTER);

        return finalLayout;
    }

    private ImageView createPosterImageView(String posterPath) {
        if (posterPath == null || posterPath.isEmpty()) {
            ImageView placeholder = new ImageView();
            placeholder.setFitWidth(100);
            placeholder.setFitHeight(150);
            return placeholder;
        }


        String baseUrl = "https://image.tmdb.org/t/p/w200";
        String imageUrl = baseUrl + posterPath;


        Image image = new Image(imageUrl, true);

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(150);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(5);
        imageView.setEffect(shadow);

        return imageView;
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        Label loadingLabel = new Label("Searching for \"" + query + "\"...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
        ProgressIndicator indicator = new ProgressIndicator();
        VBox loadingBox = new VBox(20, loadingLabel, indicator);
        loadingBox.setAlignment(Pos.CENTER);
        root.setCenter(loadingBox);

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                String jsonBody = new Gson().toJson(query);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/search"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        Gson gson = new Gson();
                        MovieResponse movieResponse = gson.fromJson(response.body(), MovieResponse.class);

                        if (movieResponse != null && movieResponse.results != null && !movieResponse.results.isEmpty()) {
                            VBox resultsUI = buildMovieListUI("Search Results: " + query, movieResponse);
                            root.setCenter(resultsUI);
                        } else {
                            Label errorLabel = new Label("No movies found for: " + query);
                            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                            VBox errorBox = new VBox(errorLabel);
                            errorBox.setAlignment(Pos.CENTER);
                            root.setCenter(errorBox);
                        }
                    } else {
                        Label errorLabel = new Label("Error during search: " + response.statusCode());
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                        VBox errorBox = new VBox(errorLabel);
                        errorBox.setAlignment(Pos.CENTER);
                        root.setCenter(errorBox);
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    Label errorLabel = new Label("Connection Error: " + ex.getMessage());
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                    VBox errorBox = new VBox(errorLabel);
                    errorBox.setAlignment(Pos.CENTER);
                    root.setCenter(errorBox);
                });
            }
        }).start();
    }

    private void makeButtonAnimated(Button btn, boolean isRedButton) {
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.10);
            btn.setScaleY(1.10);
            if (isRedButton) {
                double radius = btn.getBackground().getFills().getFirst().getRadii().getTopLeftHorizontalRadius();
                btn.setStyle("-fx-background-color: #ff1f2c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: " + radius + ";");
            }
        });
        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            if (isRedButton) {
                double radius = btn.getBackground().getFills().getFirst().getRadii().getTopLeftHorizontalRadius();
                btn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: " + radius + ";");
            }
        });
    }
    private HBox createHeader() {
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

        Button quizBtn = new Button("Quiz");
        quizBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; ");
        quizBtn.setOnAction(event -> startQuizSession());
        makeButtonAnimated(quizBtn, false);

        // --- NEW LOGIC START ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(15);
        header.getChildren().addAll(logoLabel, spacer, homeBtn, top10Btn, quizBtn);

        // Check if user is logged in using our new Session class
        if (UserSession.getInstance().isLoggedIn()) {
            // 1. Show Welcome Message
            Label welcomeUser = new Label("Welcome, " + UserSession.getInstance().getUsername());
            welcomeUser.setStyle("-fx-text-fill: #E50914; -fx-font-weight: bold; -fx-font-size: 14px;");

            // 2. Show Logout Button
            Button logoutBtn = new Button("Logout");
            logoutBtn.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-radius: 5; -fx-text-fill: white; -fx-cursor: hand;");
            logoutBtn.setOnAction(e -> {
                UserSession.getInstance().cleanUserSession(); // Clear session
                showHomeView(); // Refresh view
            });

            header.getChildren().addAll(welcomeUser, logoutBtn);

        } else {
            // 3. If NOT logged in, show Login Button
            Button loginBtn = new Button("Login / Register");
            loginBtn.setOnAction(event -> showLoginView());
            loginBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
            makeButtonAnimated(loginBtn, true);

            header.getChildren().add(loginBtn);
        }
        // --- NEW LOGIC END ---

        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");

        return header;
    }


    private void startQuizSession() {
        score = 0;
        currentQuestionIndex = 0;
        loadedQuestions = null; // Καθαρισμός


        Label loadingLabel = new Label("Generating Quiz...\nPlease wait, this takes a few seconds!");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-text-alignment: center;");
        ProgressIndicator indicator = new ProgressIndicator();

        VBox loadingBox = new VBox(20, loadingLabel, indicator);
        loadingBox.setAlignment(Pos.CENTER);
        root.setCenter(loadingBox);


        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/quiz/batch"))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        Gson gson = new Gson();
                        java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<java.util.List<com.cinematch.cinematchbackend.model.QuizQuestion>>(){}.getType();

                        loadedQuestions = gson.fromJson(response.body(), listType);

                        if (loadedQuestions != null && !loadedQuestions.isEmpty()) {
                            loadNextQuestion();
                        } else {
                            loadingLabel.setText("Failed to load questions.");
                        }
                    } else {
                        loadingLabel.setText("Error from server: " + response.statusCode());
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> loadingLabel.setText("Connection Error: " + ex.getMessage()));
            }
        }).start();
    }


    private void loadNextQuestion() {
        if (currentQuestionIndex >= loadedQuestions.size()) {
            showQuizResult();
            return;
        }


        com.cinematch.cinematchbackend.model.QuizQuestion q = loadedQuestions.get(currentQuestionIndex);


        currentQuestionIndex++;


        displayQuestionUI(q);
    }


    private void displayQuestionUI(com.cinematch.cinematchbackend.model.QuizQuestion q) {


        Label headerLabel = new Label("Question " + currentQuestionIndex + " / " + TOTAL_QUESTIONS);
        headerLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 16px;");


        Label questionLabel = new Label(q.question);
        questionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(1000);
        questionLabel.setAlignment(Pos.CENTER);


        VBox optionsBox = new VBox(15);
        optionsBox.setAlignment(Pos.CENTER);


        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


        Button nextBtn = new Button(currentQuestionIndex == TOTAL_QUESTIONS ? "Look at the results" : "Next Question");
        nextBtn.setVisible(false);
        nextBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
        nextBtn.setOnAction(e -> loadNextQuestion());


        for (String option : q.options) {
            Button optionBtn = new Button(option);
            optionBtn.setPrefWidth(400);
            optionBtn.setPrefHeight(45);
            optionBtn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;");


            optionBtn.setOnAction(event -> {

                if (option.equals(q.correctAnswer)) {
                    score++;

                    resultLabel.setText(" Correct! The answer is: " + q.correctAnswer);
                    resultLabel.setStyle("-fx-text-fill: lightgreen; -fx-font-size: 18px; -fx-font-weight: bold;");
                    optionBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-background-radius: 5;");
                } else {

                    resultLabel.setText(" Wrong! The correct answer is: " + q.correctAnswer);
                    resultLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 18px; -fx-font-weight: bold;");
                    optionBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-background-radius: 5;");
                }


                optionsBox.setDisable(true);

                nextBtn.setVisible(true);
            });

            optionsBox.getChildren().add(optionBtn);
        }


        VBox layout = new VBox(25, headerLabel, questionLabel, optionsBox, resultLabel, nextBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        root.setCenter(layout);
    }


    private void showQuizResult() {
        Label title = new Label(" End of Quiz !");
        title.setStyle("-fx-text-fill: #E50914; -fx-font-size: 32px; -fx-font-weight: bold;");

        Label scoreLabel = new Label("Your Score: " + score + " / " + TOTAL_QUESTIONS);
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        String message;
        if (score == 5) message = "Perfect!!!";
        else if (score >= 3) message = "Great Job!!";
        else message = "Nice Try";

        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 18px;");

        Button playAgainBtn = new Button("Play Again");
        playAgainBtn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
        playAgainBtn.setOnAction(e -> startQuizSession());

        Button homeBtn = new Button("Back to Homepage");
        homeBtn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
        homeBtn.setOnAction(e -> showHomeView());

        HBox buttons = new HBox(20, playAgainBtn, homeBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(30, title, scoreLabel, msgLabel, buttons);
        layout.setAlignment(Pos.CENTER);

        root.setCenter(layout);
    }

    public static void main(String[] args) {
        launch();
    }
}
