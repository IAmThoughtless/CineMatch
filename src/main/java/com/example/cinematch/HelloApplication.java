package com.example.cinematch;

import com.cinematch.cinematchbackend.model.Movie.MovieResponse;
import com.cinematch.cinematchbackend.model.Movie.Movie;
import com.cinematch.cinematchbackend.model.Comments_Reviews.Review;
import com.cinematch.cinematchbackend.model.Quiz.QuizQuestion;
import com.cinematch.cinematchbackend.model.Star.UserStar;
import com.cinematch.cinematchbackend.model.User;
import com.cinematch.cinematchbackend.model.Comments_Reviews.UserReview;
import com.cinematch.cinematchbackend.model.Movie.MovieWithReviews;
import com.cinematch.cinematchbackend.model.Quiz.LeaderboardDTO;
import com.cinematch.cinematchbackend.model.Comments_Reviews.Comment;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Hyperlink;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HelloApplication extends Application {
    // Shared HttpClient to prevent resource exhaustion
    private static final HttpClient client = HttpClient.newHttpClient();

    private VBox whatsHotContainer;
    private VBox lastMovieListView;
    private BorderPane root;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private final int TOTAL_QUESTIONS = 5;
    private java.util.List<QuizQuestion> loadedQuestions;
    private final Map<String, Integer> genreMap = Map.of(
            "Animation",16,
            "Action", 28,
            "Comedy", 35,
            "Drama", 18,
            "Horror", 27,
            "Sci-Fi", 878
    );

    // Custom Gson instance with LocalDateTime adapter
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
            })
            .registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>() {
                @Override
                public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return java.util.Base64.getDecoder().decode(json.getAsString());
                }
            })
            .registerTypeAdapter(byte[].class, new JsonSerializer<byte[]>() {
                @Override
                public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(java.util.Base64.getEncoder().encodeToString(src));
                }
            })
            .create();

    public MenuButton createGenreMenuButton() {
        MenuButton genresMenuButton = new MenuButton("GENRES");
        genresMenuButton.setPadding(new Insets(0));
        genresMenuButton.setTextFill(Color.web("#E50914"));
        genresMenuButton.setStyle(
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 10 0 0;");

        // "All" button
        Label allLabel = new Label("All");
        allLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        HBox allContainer = new HBox(allLabel);
        allContainer.setPadding(new Insets(2, 5, 2, 5));
        allContainer.setStyle("-fx-background-color: #141E30;");
        allContainer.setAlignment(Pos.CENTER_LEFT);
        allContainer.setPrefWidth(120);
        allContainer.setOnMouseEntered(e -> allContainer.setStyle("-fx-background-color: #E50914;" + "-fx-cursor: hand;"));
        allContainer.setOnMouseExited(e -> allContainer.setStyle("-fx-background-color: #141E30;"));
        CustomMenuItem allItem = new CustomMenuItem(allContainer);
        allItem.setHideOnClick(true);
        allItem.setStyle("-fx-background-color: transparent;");
        allContainer.setOnMouseClicked(e -> {
            loadWhatsHotMovies(whatsHotContainer);
        });
        genresMenuButton.getItems().add(allItem);

        for (Map.Entry<String, Integer> entry : genreMap.entrySet()) {
            String genreName = entry.getKey();

            Label label = new Label(genreName);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

            HBox container = new HBox(label);
            container.setPadding(new Insets(2, 5, 2, 5));
            container.setStyle("-fx-background-color: #141E30;");
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPrefWidth(120);

            container.setOnMouseEntered(e -> container.setStyle("-fx-background-color: #E50914;"+"-fx-cursor: hand;")); // Hover color
            container.setOnMouseExited(e -> container.setStyle("-fx-background-color: #141E30;"));

            CustomMenuItem item = new CustomMenuItem(container);
            item.setHideOnClick(true);
            item.setStyle("-fx-background-color: transparent;");
            
            container.setOnMouseClicked(e -> {
                handleGenreSelection(genreName);
            });

            genresMenuButton.getItems().add(item);
        }
        return genresMenuButton;
    }

    private void handleGenreSelection(String selectedGenreName) {

        int genreId = genreMap.get(selectedGenreName);

        if (whatsHotContainer != null) {
            loadWhatsHotMoviesByGenre(whatsHotContainer, selectedGenreName, genreId);
        }


    }


    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        // --- Header Section ---
        Label logoLabel = new Label("CineMatch");
        logoLabel.setStyle("-fx-text-fill: #E50914;");
        logoLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));

        logoLabel.setPadding(new Insets(0));

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
        quizBtn.setOnAction(event -> showQuizSelectionView());
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

        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setTitle("CineMatch App");
        primaryStage.setScene(scene);
        primaryStage.show();

        showHomeView();
    }

    private void showHomeView() {
        lastMovieListView = null;

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


        whatsHotContainer = new VBox(20);
        whatsHotContainer.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(whatsHotContainer, new Insets(40, 0, 0, 0));

        VBox homeContent = new VBox(10, welcomeLabel, subTitle, searchBox, whatsHotContainer);
        homeContent.setAlignment(Pos.TOP_CENTER);
        homeContent.setPadding(new Insets(30, 20, 20, 20));

        ScrollPane scrollPane = new ScrollPane(homeContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPannable(true);

        BorderPane homeLayout = new BorderPane();
        homeLayout.setCenter(scrollPane);


        MenuButton genresMenuButton = createGenreMenuButton();
        VBox.setMargin(genresMenuButton, new Insets(225, 0, 0, 0));
        VBox sidebarContainer = new VBox(10);
        sidebarContainer.setPadding(new Insets(20, 2, 20, 2));
        sidebarContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");
        sidebarContainer.getChildren().addAll(genresMenuButton);
        homeLayout.setLeft(sidebarContainer);
        root.setCenter(homeLayout);

        loadWhatsHotMovies(whatsHotContainer);
    }

    private void loadWhatsHotMoviesByGenre(VBox targetContainer, String genreName, int genreId) {
        Label loadingLabel = new Label("Loading " + genreName + " movies...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        ProgressIndicator indicator = new ProgressIndicator();
        VBox loadingBox = new VBox(10, loadingLabel, indicator);
        loadingBox.setAlignment(Pos.CENTER);
        targetContainer.getChildren().clear();
        targetContainer.getChildren().add(loadingBox);

        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/genre/" + genreId))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    targetContainer.getChildren().clear();
                    MovieResponse movies = gson.fromJson(response.body(), MovieResponse.class);

                    if (response.statusCode() != 200 || movies == null || movies.results == null) {
                        Label errorLabel = new Label("Could not fetch movies for " + genreName);
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                        targetContainer.getChildren().add(errorLabel);
                    } else {
                        VBox whatsHotSection = buildCompactMovieListUI("🔥 " + genreName + " 🔥", movies);
                        targetContainer.getChildren().add(whatsHotSection);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> {
                    targetContainer.getChildren().clear();
                    Label errorLabel = new Label("Connection Error: " + ex.getMessage());
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                    targetContainer.getChildren().add(errorLabel);
                });
            }
        }).start();
    }

    private void loadWhatsHotMovies(VBox targetContainer) {
        Label loadingLabel = new Label("Loading What's Hot 🔥...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        ProgressIndicator indicator = new ProgressIndicator();
        VBox loadingBox = new VBox(10, loadingLabel, indicator);
        loadingBox.setAlignment(Pos.CENTER);
        targetContainer.getChildren().add(loadingBox);

        new Thread(() -> {
            try {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/whats-hot"))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    targetContainer.getChildren().clear();
                    MovieResponse movies = gson.fromJson(response.body(), MovieResponse.class);

                    if (response.statusCode() != 200 || movies == null || movies.results == null) {
                        Label errorLabel = new Label("Could not fetch What's Hot movies. Check API.");
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                        targetContainer.getChildren().add(errorLabel);
                    }
                    else {

                        VBox whatsHotSection = buildCompactMovieListUI("🔥 What's Hot 🔥", movies);
                        targetContainer.getChildren().add(whatsHotSection);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> {
                    targetContainer.getChildren().clear();
                    Label errorLabel = new Label("Connection Error loading What's Hot: " + ex.getMessage());
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                    targetContainer.getChildren().add(errorLabel);
                });
            }
        }).start();
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
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/top-10"))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Platform.runLater(() -> {
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
                        lastMovieListView = top10Content;
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

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                signInBtn.fire();
            }
        });
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                signInBtn.fire();
            }
        });

        signInBtn.setOnAction(event -> {
            signInBtn.setDisable(true);
            String username = usernameField.getText();
            String password = passwordField.getText();

            new Thread(() -> {
                try {
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);

                    String jsonBody = gson.toJson(user);

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
                            User loggedInUser = gson.fromJson(response.body(), User.class);
                            UserSession.getInstance().setUsername(loggedInUser.getUsername());
                            UserSession.getInstance().setUserId(loggedInUser.getId());
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

        // --- NEW LOGIC: Handle Enter Key ---
        confirmPassField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                registerBtn.fire();
            }
        });
        passField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                registerBtn.fire();
            }
        });
        userField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                registerBtn.fire();
            }
        });
        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                registerBtn.fire();
            }
        });

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
                    String jsonBody = gson.toJson(newUser);

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

    private VBox buildCompactMovieListUI(String headerText, MovieResponse movieResponse) {

        Label titleLabel = new Label(headerText);
        titleLabel.setStyle("-fx-text-fill: #E50914; -fx-font-size: 24px; -fx-font-weight: bold;");
        VBox.setMargin(titleLabel, new Insets(0, 0, 10, 50));

        // 1. HBox creation for movie cards
        HBox movieRow = new HBox(18); // Padding between movies
        movieRow.setPadding(new Insets(0, 2, 0, 2));

        if (movieResponse == null || movieResponse.results == null || movieResponse.results.isEmpty()) {
            Label noResultsLabel = new Label("No movies found for this section.");
            noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            return new VBox(20, titleLabel, noResultsLabel);
        }

        // 2. Creation and Addition of movie cards
        int limit = Math.min(20, movieResponse.results.size());

        for (int i = 0; i < limit; i++) {
            Movie m = movieResponse.results.get(i);

            // Creation of a Movie Card

            ImageView posterView = createPosterImageView(m.getPoster_path());
            posterView.setFitWidth(150);
            posterView.setFitHeight(225);

            Label movieTitle = new Label(m.getTitle());
            movieTitle.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
            movieTitle.setWrapText(true);
            movieTitle.setMaxWidth(150);
            movieTitle.setMaxHeight(40);

            Label rating = new Label(String.format("⭐ %.1f", m.getVote_average()));
            rating.setStyle("-fx-text-fill: #E50914; -fx-font-size: 12px;");

            VBox movieCard = new VBox(5, posterView, movieTitle, rating);
            movieCard.setPrefWidth(150);
            movieCard.setAlignment(Pos.TOP_LEFT);
            movieCard.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");

            // Hover effects και click action
            movieCard.setOnMouseClicked(event -> showMovieDetails(m));
            movieCard.setOnMouseEntered(e -> movieCard.setStyle("-fx-cursor: hand; -fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 5;"));
            movieCard.setOnMouseExited(e -> movieCard.setStyle("-fx-cursor: hand; -fx-background-color: transparent;"));

            movieRow.getChildren().add(movieCard);
        }

        ScrollPane horizontalScrollPane = new ScrollPane(movieRow);
        horizontalScrollPane.setFitToHeight(true);
        horizontalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        horizontalScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        horizontalScrollPane.setPrefHeight(350);
        horizontalScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Button leftArrow = new Button("<");
        Button rightArrow = new Button(">");

        String arrowStyle = "-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 15;";
        leftArrow.setStyle(arrowStyle);
        rightArrow.setStyle(arrowStyle);

        leftArrow.setPrefSize(40, 40);
        rightArrow.setPrefSize(40, 40);
        leftArrow.setStyle(arrowStyle + "-fx-background-radius: 20;");
        rightArrow.setStyle(arrowStyle + "-fx-background-radius: 20;");

        leftArrow.setOnAction(e -> {
            double targetHValue = Math.max(0, horizontalScrollPane.getHvalue() - 0.3334);
            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(horizontalScrollPane.hvalueProperty(), targetHValue);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(400), keyValue));
            timeline.play();
        });
        rightArrow.setOnAction(e -> {
            double targetHValue = Math.min(1, horizontalScrollPane.getHvalue() + 0.3334);
            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(horizontalScrollPane.hvalueProperty(), targetHValue);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(400), keyValue));
            timeline.play();
        });

        // Hover effects for arrows
        leftArrow.setOnMouseEntered(e -> leftArrow.setStyle(arrowStyle + "-fx-background-radius: 20; -fx-background-color: rgba(229, 9, 20, 0.8);"));
        leftArrow.setOnMouseExited(e -> leftArrow.setStyle(arrowStyle + "-fx-background-radius: 20;"));

        rightArrow.setOnMouseEntered(e -> rightArrow.setStyle(arrowStyle + "-fx-background-radius: 20; -fx-background-color: rgba(229, 9, 20, 0.8);"));
        rightArrow.setOnMouseExited(e -> rightArrow.setStyle(arrowStyle + "-fx-background-radius: 20;"));


        HBox containerWithArrows = new HBox(5, leftArrow, horizontalScrollPane, rightArrow);
        containerWithArrows.setAlignment(Pos.CENTER);
        HBox.setHgrow(horizontalScrollPane, Priority.ALWAYS);


        VBox finalLayout = new VBox(10, titleLabel, containerWithArrows);
        finalLayout.setAlignment(Pos.TOP_LEFT);
        finalLayout.setMaxWidth(1100);

        return finalLayout;
    }

    private VBox buildMovieListUI(String headerText, MovieResponse movieResponse) {

        Label titleLabel = new Label(headerText);
        titleLabel.setStyle("-fx-text-fill: #E50914; -fx-font-size: 32px; -fx-font-weight: bold;");

        VBox movieListView = new VBox(15);
        movieListView.setPadding(new Insets(30));
        movieListView.setAlignment(Pos.TOP_CENTER);
        movieListView.setMaxWidth(800);

        if (movieResponse == null || movieResponse.results == null || movieResponse.results.isEmpty()) {
            Label noResultsLabel = new Label("No movies found.");
            noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            movieListView.getChildren().add(noResultsLabel);
        } else {
            int limit = Math.min(20, movieResponse.results.size());

            for (int i = 0; i < limit; i++) {
                Movie m = movieResponse.results.get(i);

                ImageView posterView = createPosterImageView(m.getPoster_path());

                posterView.setFitWidth(80);
                posterView.setFitHeight(120);

                Label movieTitle = new Label(m.getTitle());
                movieTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

                String date = (m.getRelease_date() != null && !m.getRelease_date().isEmpty()) ? m.getRelease_date() : "N/A";
                Label movieDetails = new Label(String.format("⭐ %.1f | %s", m.getVote_average(), date));
                movieDetails.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

                Label clickHint = new Label("Details ...");
                clickHint.setStyle("-fx-text-fill: #555; -fx-font-style: italic; -fx-font-size: 12px;");

                VBox textContent = new VBox(5, movieTitle, movieDetails, clickHint);
                textContent.setAlignment(Pos.CENTER_LEFT);

                HBox movieCard = new HBox(20.0, posterView, textContent);
                movieCard.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(textContent, Priority.ALWAYS);


                movieCard.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");


                movieCard.setOnMouseClicked(event -> showMovieDetails(m));


                movieCard.setOnMouseEntered(e -> movieCard.setStyle("-fx-cursor: hand; -fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10;"));
                movieCard.setOnMouseExited(e -> movieCard.setStyle("-fx-cursor: hand; -fx-background-color: transparent;"));


                movieListView.getChildren().add(movieCard);

                if (i < limit - 1) {
                    Region separator = new Region();
                    separator.setPrefHeight(1);
                    separator.setStyle("-fx-background-color: #333;");
                    movieListView.getChildren().add(separator);
                }
            }
        }

        ScrollPane scrollPane = new ScrollPane(movieListView);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPannable(true);

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
                String jsonBody = gson.toJson(query);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/search"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        MovieResponse movieResponse = gson.fromJson(response.body(), MovieResponse.class);

                        if (movieResponse != null && movieResponse.results != null && !movieResponse.results.isEmpty()) {
                            VBox resultsUI = buildMovieListUI("Search Results: " + query, movieResponse);
                            lastMovieListView = resultsUI;
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

        final String originalStyle = btn.getStyle();

        btn.setOnMouseEntered(e -> {

            btn.setScaleX(1.05);
            btn.setScaleY(1.05);

            if (isRedButton) {

                if (originalStyle.contains("#E50914")) {
                    btn.setStyle(originalStyle.replace("#E50914", "#ff1f2c"));
                } else {
                    // Ασφάλεια: Αν δε βρει το χρώμα, απλά προσθέτει το νέο χρώμα στο τέλος
                    btn.setStyle(originalStyle + "-fx-background-color: #ff1f2c;");
                }
            }
        });

        btn.setOnMouseExited(e -> {

            btn.setScaleX(1.0);
            btn.setScaleY(1.0);


            btn.setStyle(originalStyle);
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

        // --- HOME BUTTON (Reset history) ---
        Button homeBtn = new Button("Home Page");
        homeBtn.setOnAction(event -> {
            lastMovieListView = null; // ΚΑΘΑΡΙΣΜΟΣ ΙΣΤΟΡΙΚΟΥ
            showHomeView();
        });
        homeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        makeButtonAnimated(homeBtn, false);

        // --- TOP 10 BUTTON (Keep history) ---
        Button top10Btn = new Button("Top 10");
        top10Btn.setOnAction(event -> {
            // Εδώ ΔΕΝ βάζουμε null, γιατί θέλουμε το Back να μας γυρνάει στη λίστα του Top 10
            showTop10View();
        });
        top10Btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        makeButtonAnimated(top10Btn, false);

        // --- QUIZ BUTTON (Reset history) ---
        Button quizBtn = new Button("Quiz");
        quizBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; ");
        quizBtn.setOnAction(event -> {
            lastMovieListView = null; // ΚΑΘΑΡΙΣΜΟΣ ΙΣΤΟΡΙΚΟΥ
            showQuizSelectionView();
        });
        makeButtonAnimated(quizBtn, false);

        // --- LEADERBOARD BUTTON (Reset history) ---
        Button leaderboardBtn = new Button("Leaderboard");
        leaderboardBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; ");
        leaderboardBtn.setOnAction(event -> {
            lastMovieListView = null; // ΚΑΘΑΡΙΣΜΟΣ ΙΣΤΟΡΙΚΟΥ
            showLeaderboardView();
        });
        makeButtonAnimated(leaderboardBtn, false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(15);
        header.getChildren().addAll(logoLabel, spacer, homeBtn, top10Btn, quizBtn, leaderboardBtn);

        if (UserSession.getInstance().isLoggedIn()) {

            // --- LOOKALAIKE BUTTON (Reset history) ---
            Button aiIntegrationBtn = new Button("Lookalike");
            aiIntegrationBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            aiIntegrationBtn.setOnAction(event -> {
                lastMovieListView = null; // ΚΑΘΑΡΙΣΜΟΣ ΙΣΤΟΡΙΚΟΥ
                showAIIntegrationView();
            });
            makeButtonAnimated(aiIntegrationBtn, false);

            // --- MY STARS BUTTON (Keep history) ---
            Button myStarsBtn = new Button("My Stars");
            myStarsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            myStarsBtn.setOnAction(event -> {
                // Εδώ το My Stars λειτουργεί σαν λίστα, άρα το αφήνουμε να ορίζεται μέσα στη showMyStarsView
                showMyStarsView();
            });
            makeButtonAnimated(myStarsBtn, false);

            // --- SUGGESTIONS BUTTON (Keep history) ---
            Button suggestionsBtn = new Button("Suggestions");
            suggestionsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            suggestionsBtn.setOnAction(event -> {
                showSuggestionsView();
            });
            makeButtonAnimated(suggestionsBtn, false);

            Label welcomeUser = new Label("Welcome, " + UserSession.getInstance().getUsername());
            welcomeUser.setStyle("-fx-text-fill: #E50914; -fx-font-weight: bold; -fx-font-size: 14px;");

            Button logoutBtn = new Button("Logout");
            logoutBtn.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-radius: 5; -fx-text-fill: white; -fx-cursor: hand;");
            logoutBtn.setOnAction(e -> {
                lastMovieListView = null; // ΚΑΘΑΡΙΣΜΟΣ ΙΣΤΟΡΙΚΟΥ
                UserSession.getInstance().cleanUserSession();
                showHomeView();
            });

            header.getChildren().addAll(aiIntegrationBtn, myStarsBtn, suggestionsBtn, welcomeUser, logoutBtn);

        } else {
            Button loginBtn = new Button("Login / Register");
            loginBtn.setOnAction(event -> {
                lastMovieListView = null; // ΚΑΘΑΡΙΣΜΟΣ ΙΣΤΟΡΙΚΟΥ
                showLoginView();
            });
            loginBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
            makeButtonAnimated(loginBtn, true);

            header.getChildren().add(loginBtn);
        }

        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");

        return header;
    }


    private void startQuizSession(boolean isPersonalized) {
        score = 0;
        currentQuestionIndex = 0;
        loadedQuestions = null;

        Label loadingLabel = new Label(isPersonalized ? "Generating Quiz from your Favorites..." : "Generating General Movie Quiz...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-text-alignment: center;");
        ProgressIndicator indicator = new ProgressIndicator();

        VBox loadingBox = new VBox(20, loadingLabel, indicator);
        loadingBox.setAlignment(Pos.CENTER);
        root.setCenter(loadingBox);

        new Thread(() -> {
            try {

                // URL choice depending on quiz type
                String url;
                if (isPersonalized) {
                    Long userId = UserSession.getInstance().getUserId();
                    url = "http://localhost:8080/api/quiz/personalized/" + userId;
                } else {
                    url = "http://localhost:8080/api/quiz/batch";
                }

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        java.lang.reflect.Type listType = new TypeToken<java.util.List<QuizQuestion>>(){}.getType();
                        loadedQuestions = gson.fromJson(response.body(), listType);

                        if (loadedQuestions != null && !loadedQuestions.isEmpty()) {
                            loadNextQuestion();
                        } else {
                            loadingLabel.setText("Failed to load questions.");
                        }
                    } else if (response.statusCode() == 400) {
                        loadingLabel.setText("Not enough favorites! Star at least 3 movies.");
                        loadingLabel.setStyle("-fx-text-fill: #E50914; -fx-font-size: 20px;");
                        Button backBtn = new Button("Go Back");
                        backBtn.setOnAction(e -> showQuizSelectionView());
                        loadingBox.getChildren().add(backBtn);
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


        QuizQuestion q = loadedQuestions.get(currentQuestionIndex);


        currentQuestionIndex++;


        displayQuestionUI(q);
    }


    private void displayQuestionUI(QuizQuestion q) {


        Label headerLabel = new Label("Question " + currentQuestionIndex + " / " + TOTAL_QUESTIONS);
        headerLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 16px;");


        Label questionLabel = new Label(q.question);
        questionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(500);
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
        if (UserSession.getInstance().isLoggedIn()) {
            submitQuizScore();
        }

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
        playAgainBtn.setOnAction(e -> showQuizSelectionView());

        Button homeBtn = new Button("Back to Homepage");
        homeBtn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
        homeBtn.setOnAction(e -> showHomeView());

        HBox buttons = new HBox(20, playAgainBtn, homeBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox layout = new VBox(30, title, scoreLabel, msgLabel, buttons);
        layout.setAlignment(Pos.CENTER);

        root.setCenter(layout);
    }

    private void submitQuizScore() {
        new Thread(() -> {
            try {
                String jsonBody = String.format("{\"userId\": %d, \"score\": %d, \"maxScore\": %d}",
                        UserSession.getInstance().getUserId(), score, TOTAL_QUESTIONS);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/quiz/submit"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void showLeaderboardView() {
        Label loadingLabel = new Label("Fetching Leaderboard...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
        ProgressIndicator indicator = new ProgressIndicator();
        VBox loadingBox = new VBox(20, loadingLabel, indicator);
        loadingBox.setAlignment(Pos.CENTER);
        root.setCenter(loadingBox);

        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/quiz/leaderboard"))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        java.lang.reflect.Type listType = new TypeToken<List<LeaderboardDTO>>(){}.getType();
                        List<LeaderboardDTO> leaderboard = gson.fromJson(response.body(), listType);
                        VBox leaderboardContent = buildLeaderboardUI("🏆 Leaderboard 🏆", leaderboard);
                        root.setCenter(leaderboardContent);
                    } else {
                        Label errorLabel = new Label("Could not fetch leaderboard. Check API and network connection.");
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                        VBox errorBox = new VBox(errorLabel);
                        errorBox.setAlignment(Pos.CENTER);
                        root.setCenter(errorBox);
                    }
                });

            } catch (Exception ex) {
                Label errorLabel = new Label("Could not fetch leaderboard. Check API and network connection.");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                VBox errorBox = new VBox(errorLabel);
                errorBox.setAlignment(Pos.CENTER);
                root.setCenter(errorBox);
            }
        }).start();
    }

    private VBox buildLeaderboardUI(String headerText, List<LeaderboardDTO> leaderboard) {
        Label titleLabel = new Label(headerText);
        titleLabel.setStyle("-fx-text-fill: #E50914; -fx-font-size: 32px; -fx-font-weight: bold;");
        VBox.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        VBox contentLayout = new VBox(20);
        contentLayout.setAlignment(Pos.TOP_CENTER);
        contentLayout.setPadding(new Insets(0, 0, 50, 0)); 

        if (leaderboard == null || leaderboard.isEmpty()) {
            Label noResultsLabel = new Label("No scores on the leaderboard yet.");
            noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            contentLayout.getChildren().add(noResultsLabel);
        } else {
            // 1. Podium Section (Top 3)
            HBox podiumBox = new HBox(15); 
            podiumBox.setAlignment(Pos.BOTTOM_CENTER); 
            podiumBox.setPadding(new Insets(20, 0, 30, 0));

            LeaderboardDTO first = !leaderboard.isEmpty() ? leaderboard.get(0) : null;
            LeaderboardDTO second = leaderboard.size() > 1 ? leaderboard.get(1) : null;
            LeaderboardDTO third = leaderboard.size() > 2 ? leaderboard.get(2) : null;

            if (second != null) podiumBox.getChildren().add(createPodiumStep(second, 2));
            if (first != null) podiumBox.getChildren().add(createPodiumStep(first, 1));
            if (third != null) podiumBox.getChildren().add(createPodiumStep(third, 3));

            contentLayout.getChildren().add(podiumBox);

            // 2. List Section (4th onwards)
            if (leaderboard.size() > 3) {
                VBox listContainer = new VBox(10);
                listContainer.setMaxWidth(800); 
                listContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 10; -fx-padding: 20;");
                
                for (int i = 3; i < leaderboard.size(); i++) {
                    LeaderboardDTO entry = leaderboard.get(i);
                    HBox row = createLeaderboardRow(entry, i + 1);
                    listContainer.getChildren().add(row);
                    
                    if (i < leaderboard.size() - 1) {
                        Region sep = new Region();
                        sep.setPrefHeight(1);
                        sep.setStyle("-fx-background-color: rgba(255,255,255,0.1);");
                        listContainer.getChildren().add(sep);
                    }
                }
                contentLayout.getChildren().add(listContainer);
            }
        }

        ScrollPane scrollPane = new ScrollPane(contentLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox finalLayout = new VBox(20, titleLabel, scrollPane);
        finalLayout.setPadding(new Insets(30));
        finalLayout.setAlignment(Pos.TOP_CENTER);

        return finalLayout;
    }

    private VBox createPodiumStep(LeaderboardDTO entry, int rank) {
        // Colors
        String color;
        double height;
        String emoji;
        
        if (rank == 1) {
            color = "#FFD700"; // Gold
            height = 250;
            emoji = "👑";
        } else if (rank == 2) {
            color = "#C0C0C0"; // Silver
            height = 180;
            emoji = "🥈";
        } else {
            color = "#CD7F32"; // Bronze
            height = 130;
            emoji = "🥉";
        }

        // User Info (Above the bar)
        Label usernameLabel = new Label(entry.getUsername());
        usernameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        usernameLabel.setWrapText(true);
        usernameLabel.setTextAlignment(TextAlignment.CENTER);
        usernameLabel.setMaxWidth(120);

        Label scoreLabel = new Label(entry.getScore() + " pts");
        scoreLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label dateLabel = new Label(entry.getCreatedAt());
        dateLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        VBox infoBox = new VBox(5, usernameLabel, scoreLabel, dateLabel);
        infoBox.setAlignment(Pos.BOTTOM_CENTER);
        infoBox.setPadding(new Insets(0, 0, 10, 0));

        // The Bar
        VBox bar = new VBox();
        bar.setPrefSize(100, height);
        bar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10 10 0 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");
        bar.setAlignment(Pos.CENTER);
        
        Label rankLabel = new Label(emoji);
        rankLabel.setFont(Font.font("Segoe UI Emoji", 40));
        rankLabel.setStyle("-fx-text-fill: white;");

        bar.getChildren().add(rankLabel);

        VBox step = new VBox(infoBox, bar);
        step.setAlignment(Pos.BOTTOM_CENTER);
        return step;
    }

    private HBox createLeaderboardRow(LeaderboardDTO entry, int rank) {
        Label rankLabel = new Label(rank + ".");
        rankLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 18px; -fx-font-weight: bold;");
        rankLabel.setPrefWidth(40);

        Label userLabel = new Label(entry.getUsername());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label scoreLabel = new Label(entry.getScore() + " pts");
        scoreLabel.setStyle("-fx-text-fill: #E50914; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label dateLabel = new Label(entry.getCreatedAt());
        dateLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        dateLabel.setPadding(new Insets(0, 0, 0, 15));

        HBox row = new HBox(10, rankLabel, userLabel, spacer, scoreLabel, dateLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5));
        return row;
    }

    private File selectedImageFile;

    private void showMovieDetails(Movie initialMovieData) {
        // 1. Header & Back Button
        Button backBtn = new Button("⬅ Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #E50914; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if (lastMovieListView != null) {
                root.setCenter(lastMovieListView);
            } else {
                showHomeView();
            }
        });

        // 2. Poster & Basic Info
        ImageView posterView = createPosterImageView(initialMovieData.getPoster_path());
        posterView.setFitWidth(300);
        posterView.setFitHeight(450);

        Label titleLabel = new Label(initialMovieData.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);

        String date = (initialMovieData.getRelease_date() != null && !initialMovieData.getRelease_date().isEmpty()) ? initialMovieData.getRelease_date() : "N/A";
        Label metaLabel = new Label("📅 " + date + "  |  ⭐ " + initialMovieData.getVote_average() + "/10 (" + initialMovieData.getVote_count() + " votes)");
        metaLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 16px;");

        // Facts Container
        GridPane factsGrid = new GridPane();
        factsGrid.setHgap(30); factsGrid.setVgap(10);
        factsGrid.setPadding(new Insets(10, 0, 10, 0));

        // Cast Section (Με βελάκια)
        VBox castSection = new VBox(15);
        Label castHeader = new Label("Top Cast");
        castHeader.setStyle("-fx-text-fill: #E50914; -fx-font-size: 24px; -fx-font-weight: bold;");
        HBox castBox = new HBox(15);
        castBox.setPadding(new Insets(10));
        ScrollPane castScroll = new ScrollPane(castBox);
        castScroll.setFitToHeight(true);
        castScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        castScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        castScroll.setPrefWidth(850);

        Button leftArrow = createScrollButton("<");
        Button rightArrow = createScrollButton(">");
        leftArrow.setOnAction(e -> new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(castScroll.hvalueProperty(), Math.max(0, castScroll.getHvalue() - 0.25)))).play());
        rightArrow.setOnAction(e -> new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(castScroll.hvalueProperty(), Math.min(1, castScroll.getHvalue() + 0.25)))).play());

        HBox castWithArrows = new HBox(10, leftArrow, castScroll, rightArrow);
        castWithArrows.setAlignment(Pos.CENTER);
        castSection.getChildren().addAll(castHeader, castWithArrows);

        Label overviewLabel = new Label(initialMovieData.getOverview());
        overviewLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        overviewLabel.setWrapText(true); overviewLabel.setMaxWidth(600);

        // --- BUTTONS SETUP ---
        Button starBtn = new Button("Loading...");
        starBtn.setDisable(true);

        Button trailerBtn = new Button("🎬 Watch Trailer");
        trailerBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 8 15 8 15;");
        trailerBtn.setVisible(false);
        trailerBtn.setManaged(false);

        if (UserSession.getInstance().isLoggedIn()) {
            new Thread(() -> {
                boolean isStarred = isMovieStarred(initialMovieData.getId());
                Platform.runLater(() -> {
                    starBtn.setDisable(false);
                    if (isStarred) setupUnstarButton(starBtn, initialMovieData);
                    else setupStarButton(starBtn, initialMovieData);
                });
            }).start();
        } else {
            starBtn.setText("⭐ Star Movie");
            starBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 8 15 8 15;");
            starBtn.setDisable(false);
            starBtn.setOnAction(ev -> showLoginView());
        }

        HBox actionButtons = new HBox(15, starBtn, trailerBtn);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(20, titleLabel, metaLabel, factsGrid, overviewLabel, actionButtons);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox topContent = new HBox(40, posterView, infoBox);
        topContent.setAlignment(Pos.CENTER);
        topContent.setPadding(new Insets(0, 0, 40, 0));

        // REVIEWS & COMMENTS CONTAINERS
        VBox reviewsContainer = new VBox(15);
        reviewsContainer.setAlignment(Pos.TOP_LEFT); reviewsContainer.setMaxWidth(800);
        Label reviewsHeader = new Label("User Reviews");
        reviewsHeader.setStyle("-fx-text-fill: #E50914; -fx-font-size: 24px; -fx-font-weight: bold;");
        VBox userReviewsBox = new VBox(10);
        Label loadingReviewsLabel = new Label("Loading details & reviews...");
        loadingReviewsLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
        reviewsContainer.getChildren().addAll(reviewsHeader, userReviewsBox, loadingReviewsLabel);

        VBox commentsContainer = new VBox(15);
        commentsContainer.setAlignment(Pos.TOP_LEFT); commentsContainer.setMaxWidth(800);
        Label commentsHeader = new Label("Comments");
        commentsHeader.setStyle("-fx-text-fill: #E50914; -fx-font-size: 24px; -fx-font-weight: bold;");
        VBox commentsBox = new VBox(10);
        Label loadingCommentsLabel = new Label("Loading comments...");
        loadingCommentsLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
        commentsContainer.getChildren().addAll(commentsHeader, commentsBox, loadingCommentsLabel);

        // Comment Input (Με έλεγχο login)
        TextArea commentTextArea = new TextArea();
        commentTextArea.setPromptText("Write your comment here...");
        commentTextArea.setWrapText(true); commentTextArea.setPrefHeight(100);
        commentTextArea.setStyle("-fx-control-inner-background:#333; -fx-prompt-text-fill: white; -fx-text-fill: white; -fx-background-radius: 5;");
        commentTextArea.setOnMouseClicked(e -> { if (!UserSession.getInstance().isLoggedIn()) showLoginView(); });

        Button uploadImageBtn = new Button("Upload Image");
        Label selectedImageLabel = new Label("No image selected");
        selectedImageLabel.setStyle("-fx-text-fill: #cccccc;");
        uploadImageBtn.setOnAction(e -> {
            if (!UserSession.getInstance().isLoggedIn()) { showLoginView(); return; }
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = fc.showOpenDialog(root.getScene().getWindow());
            if (file != null) { selectedImageFile = file; selectedImageLabel.setText(file.getName()); }
        });
        Button submitCommentBtn = new Button("Submit Comment");
        submitCommentBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        submitCommentBtn.setOnAction(e -> {
            if (!UserSession.getInstance().isLoggedIn()) { showLoginView(); return; }
            String text = commentTextArea.getText();
            if (text != null && !text.trim().isEmpty()) {
                submitComment(initialMovieData, text, selectedImageFile);
                selectedImageFile = null; selectedImageLabel.setText("No image selected"); commentTextArea.clear();
            }
        });
        HBox imgBox = new HBox(10, uploadImageBtn, selectedImageLabel); imgBox.setAlignment(Pos.CENTER_LEFT);
        commentsContainer.getChildren().add(new VBox(10, new Label("Add Your Comment"), commentTextArea, imgBox, submitCommentBtn));

        VBox mainContent = new VBox(30, topContent);
        mainContent.setAlignment(Pos.TOP_CENTER); mainContent.setPadding(new Insets(40));
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true); scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(new VBox(20, backBtn, scrollPane));

        // --- API LOADING (MOVIE DETAILS & TRAILER) ---
        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/movie/" + initialMovieData.getId())).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        com.google.gson.JsonObject rootObj = gson.fromJson(response.body(), com.google.gson.JsonObject.class);
                        MovieWithReviews movieWithReviews = gson.fromJson(response.body(), MovieWithReviews.class);
                        Movie fullMovie = movieWithReviews.getMovie();

                        // Ανάκτηση Trailer Key (Διπλός έλεγχος)
                        String tKey = fullMovie.getTrailerKey();
                        if (tKey == null && rootObj.has("movie")) {
                            com.google.gson.JsonObject mObj = rootObj.getAsJsonObject("movie");
                            if (mObj.has("trailer_key")) {
                                tKey = mObj.get("trailer_key").getAsString();
                            }
                        }

                        if (tKey != null && !tKey.isEmpty()) {
                            final String finalKey = tKey;
                            trailerBtn.setVisible(true);
                            trailerBtn.setManaged(true);
                            trailerBtn.setOnAction(ev -> getHostServices().showDocument("https://www.youtube.com/watch?v=" + finalKey));
                        }

                        // Facts Update
                        factsGrid.getChildren().clear();
                        if (fullMovie.getBudget() > 0) factsGrid.add(createFactBox("Budget", String.format("$%,d", fullMovie.getBudget())), 0, 0);
                        if (fullMovie.getRevenue() > 0) factsGrid.add(createFactBox("Revenue", String.format("$%,d", fullMovie.getRevenue())), 1, 0);
                        if (fullMovie.getRuntime() > 0) factsGrid.add(createFactBox("Runtime", fullMovie.getRuntime() + " min"), 0, 1);

                        // Cast Update
                        castBox.getChildren().clear();
                        if (fullMovie.getCast() != null) {
                            for (Movie.CastMember actor : fullMovie.getCast()) castBox.getChildren().add(createActorCard(actor, fullMovie));
                            if (!mainContent.getChildren().contains(castSection)) mainContent.getChildren().add(1, castSection);
                        }

                        reviewsContainer.getChildren().remove(loadingReviewsLabel);
                        userReviewsBox.getChildren().clear();

                        // TMDB Reviews with Read More
                        if (fullMovie.getReviews() != null && fullMovie.getReviews().getResults() != null) {
                            for (Review r : fullMovie.getReviews().getResults()) {
                                VBox rb = new VBox(5);
                                rb.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 15; -fx-background-radius: 10;");
                                rb.setMaxWidth(750);
                                Label auth = new Label("👤 " + r.getAuthor());
                                auth.setStyle("-fx-text-fill: #ccc; -fx-font-weight: bold;");
                                Label cont = new Label();
                                cont.setStyle("-fx-text-fill: white;");
                                cont.setWrapText(true); cont.setMaxWidth(720);

                                String fullText = r.getContent();
                                if (fullText.length() > 300) {
                                    String shortText = fullText.substring(0, 300) + "...";
                                    cont.setText(shortText);
                                    Hyperlink link = new Hyperlink("Read More ⬇");
                                    link.setStyle("-fx-text-fill: #E50914; -fx-font-weight: bold;");
                                    link.setOnAction(ev -> {
                                        if (link.getText().equals("Read More ⬇")) { cont.setText(fullText); link.setText("Read Less ⬆"); }
                                        else { cont.setText(shortText); link.setText("Read More ⬇"); }
                                    });
                                    rb.getChildren().addAll(auth, cont, link);
                                } else {
                                    cont.setText(fullText);
                                    rb.getChildren().addAll(auth, cont);
                                }
                                reviewsContainer.getChildren().add(rb);
                            }
                        }
                        if (!mainContent.getChildren().contains(reviewsContainer)) mainContent.getChildren().addAll(reviewsContainer, commentsContainer);
                    }
                });
            } catch (Exception ex) { ex.printStackTrace(); }
        }).start();

        // --- LOAD USER COMMENTS ---
        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/comments/movie/" + initialMovieData.getId())).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        List<Comment> allComments = gson.fromJson(response.body(), new TypeToken<List<Comment>>(){}.getType());
                        commentsContainer.getChildren().remove(loadingCommentsLabel);
                        commentsBox.getChildren().clear();
                        for (Comment c : allComments) {
                            VBox cb = new VBox(5);
                            cb.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 15; -fx-background-radius: 10;");
                            cb.setMaxWidth(750);
                            Label u = new Label("👤 " + c.getUserName());
                            u.setStyle("-fx-text-fill: #ccc; -fx-font-weight: bold;");
                            Label t = new Label();
                            t.setStyle("-fx-text-fill: white;");
                            t.setWrapText(true); t.setMaxWidth(720);

                            String fullT = c.getText();
                            if (fullT.length() > 250) {
                                String shortT = fullT.substring(0, 250) + "...";
                                t.setText(shortT);
                                Hyperlink h = new Hyperlink("Read More ⬇");
                                h.setStyle("-fx-text-fill: #E50914; -fx-font-weight: bold;");
                                h.setOnAction(ev -> {
                                    if (h.getText().equals("Read More ⬇")) { t.setText(fullT); h.setText("Read Less ⬆"); }
                                    else { t.setText(shortT); h.setText("Read More ⬇"); }
                                });
                                cb.getChildren().addAll(u, t, h);
                            } else {
                                t.setText(fullT);
                                cb.getChildren().addAll(u, t);
                            }

                            if (c.getImage() != null && c.getImage().length > 0) {
                                try {
                                    ImageView iv = new ImageView(new Image(new ByteArrayInputStream(c.getImage())));
                                    iv.setFitWidth(300); iv.setPreserveRatio(true);
                                    cb.getChildren().add(iv);
                                } catch (Exception ignored) {}
                            }
                            commentsBox.getChildren().add(cb);
                        }
                    }
                });
            } catch (Exception ignored) {}
        }).start();
    }

    private VBox createFactBox(String title, String value) {
        Label t = new Label(title);
        t.setStyle("-fx-text-fill: #E50914; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");
        return new VBox(2, t, v);
    }

    private VBox createActorCard(Movie.CastMember actor, Movie currentMovie) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.TOP_CENTER);

        ImageView actorImg = new ImageView();
        actorImg.setFitWidth(90);
        actorImg.setFitHeight(120);

        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(90, 120);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        actorImg.setClip(clip);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setOffsetX(0);
        shadow.setOffsetY(2);
        shadow.setColor(Color.BLACK);
        actorImg.setEffect(shadow);

        String path = actor.getProfilePath();
        if (path != null && !path.isEmpty()) {

            actorImg.setImage(new Image("https://image.tmdb.org/t/p/w185" + path, true));
        } else {
            actorImg.setImage(new Image("https://www.kindpng.com/picc/m/24-248253_user-profile-default-image-png-clipart-png-download.png", true));

            actorImg.setOpacity(0.6); // Λίγο πιο διάφανο για να φαίνεται ότι λείπει
        }

        Label name = new Label(actor.getName());
        name.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        name.setWrapText(true);
        name.setMaxWidth(90);
        name.setAlignment(Pos.CENTER);
        name.setTextAlignment(TextAlignment.CENTER);

        Label role = new Label(actor.getCharacter());
        role.setStyle("-fx-text-fill: #888888; -fx-font-size: 9px;");
        role.setWrapText(true);
        role.setMaxWidth(90);
        role.setAlignment(Pos.CENTER);
        role.setTextAlignment(TextAlignment.CENTER);

        card.getChildren().addAll(actorImg, name, role);
        card.setStyle("-fx-cursor: hand;");
        card.setOnMouseClicked(e -> showMoviesByActor(actor, currentMovie));
        return card;
    }

    private void showMoviesByActor(Movie.CastMember actor, Movie previousMovie) {
        // 1. Δημιουργία του Back Button
        Button backToMovieBtn = new Button("⬅ Back to " + previousMovie.getTitle());
        backToMovieBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #E50914; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        backToMovieBtn.setOnAction(e -> showMovieDetails(previousMovie));

        Label loadingLabel = new Label("Fetching movies for " + actor.getName() + "...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
        ProgressIndicator indicator = new ProgressIndicator();

        // Εμφάνιση loading με το back button διαθέσιμο αμέσws
        VBox loadingBox = new VBox(20, backToMovieBtn, loadingLabel, indicator);
        loadingBox.setAlignment(Pos.CENTER);
        root.setCenter(loadingBox);

        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/actor/" + actor.getId()))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Platform.runLater(() -> {
                    MovieResponse movies = gson.fromJson(response.body(), MovieResponse.class);

                    if (response.statusCode() != 200 || movies == null || movies.results == null) {
                        Label errorLabel = new Label("Could not fetch movies for actor.");
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                        root.setCenter(new VBox(20, backToMovieBtn, errorLabel));
                    } else {
                        // 2. Δημιουργία της λίστας ταινιών
                        VBox actorMoviesContent = buildMovieListUI("🎬 Movies with " + actor.getName() + " 🎬", movies);

                        // 3. Σύνθεση τελικού Layout (Κουμπί + Λίστα)
                        VBox finalLayout = new VBox(15, backToMovieBtn, actorMoviesContent);
                        finalLayout.setPadding(new Insets(20));
                        finalLayout.setAlignment(Pos.TOP_LEFT);

                        lastMovieListView = finalLayout;
                        root.setCenter(finalLayout);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private Button createScrollButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: white; -fx-font-size: 20px; " +
                "-fx-font-weight: bold; -fx-background-radius: 30; -fx-cursor: hand;");
        btn.setPrefSize(40, 40);

        // Hover effect
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 30; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 30; -fx-cursor: hand;"));

        return btn;
    }

    private void submitReview(Movie movie, String reviewText) {
        new Thread(() -> {
            try {
                UserReview review = new UserReview();
                review.setTmdbId(movie.getId());
                review.setReviewText(reviewText);
                User user = new User();
                user.setId(UserSession.getInstance().getUserId());
                review.setUser(user);

                String jsonBody = gson.toJson(review);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/reviews"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    Platform.runLater(() -> {
                        showMovieDetails(movie);
                    });
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void submitComment(Movie movie, String commentText, File imageFile) {
        new Thread(() -> {
            try {
                String boundary = "---ContentBoundary" + System.currentTimeMillis();
                String lineFeed = "\r\n";
                
                List<byte[]> multipartBody = new ArrayList<>();
                
                // Add movieId
                addFormField(multipartBody, "movieId", String.valueOf(movie.getId()), boundary, lineFeed);
                // Add userName
                addFormField(multipartBody, "userName", UserSession.getInstance().getUsername(), boundary, lineFeed);
                // Add text
                addFormField(multipartBody, "text", commentText, boundary, lineFeed);
                // Add rating (default 0 for now)
                addFormField(multipartBody, "rating", "0", boundary, lineFeed);
                
                // Add image if exists
                if (imageFile != null) {
                    addFilePart(multipartBody, "image", imageFile, boundary, lineFeed);
                }
                
                // End boundary
                multipartBody.add(("--" + boundary + "--" + lineFeed).getBytes(StandardCharsets.UTF_8));
                
                // Combine all parts
                int totalSize = 0;
                for (byte[] part : multipartBody) {
                    totalSize += part.length;
                }
                byte[] requestBody = new byte[totalSize];
                int offset = 0;
                for (byte[] part : multipartBody) {
                    System.arraycopy(part, 0, requestBody, offset, part.length);
                    offset += part.length;
                }

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/comments/add"))
                        .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                        .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    Platform.runLater(() -> {
                        showMovieDetails(movie);
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void addFormField(List<byte[]> body, String name, String value, String boundary, String lineFeed) {
        String header = "--" + boundary + lineFeed +
                "Content-Disposition: form-data; name=\"" + name + "\"" + lineFeed +
                lineFeed +
                value + lineFeed;
        body.add(header.getBytes(StandardCharsets.UTF_8));
    }

    private void addFilePart(List<byte[]> body, String fieldName, File uploadFile, String boundary, String lineFeed) throws IOException {
        String fileName = uploadFile.getName();
        String header = "--" + boundary + lineFeed +
                "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"" + lineFeed +
                "Content-Type: application/octet-stream" + lineFeed +
                lineFeed;
        body.add(header.getBytes(StandardCharsets.UTF_8));
        body.add(Files.readAllBytes(uploadFile.toPath()));
        body.add(lineFeed.getBytes(StandardCharsets.UTF_8));
    }

    private UserReview getUserReview(Long tmdbId) {
        if (!UserSession.getInstance().isLoggedIn()) {
            return null;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/reviews/" + UserSession.getInstance().getUserId() + "/" + tmdbId))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null && !response.body().isEmpty()) {
                return gson.fromJson(response.body(), UserReview.class);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private void setupStarButton(Button btn, Movie m) {
        btn.setText("⭐ Star Movie");
        btn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        makeButtonAnimated(btn, true);
        btn.setOnAction(ev -> {
            starMovie(m);
            setupUnstarButton(btn, m);
        });
    }

    private void setupUnstarButton(Button btn, Movie m) {
        btn.setText("Unstar");
        btn.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        makeButtonAnimated(btn, false);
        btn.setOnAction(ev -> {
            unstarMovie(m.getId());
            setupStarButton(btn, m);
        });
    }

    private void showMyStarsView() {
        if (!UserSession.getInstance().isLoggedIn()) {
            showLoginView();
            return;
        }

        Label loadingLabel = new Label("Fetching Your Starred Movies...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
        ProgressIndicator indicator = new ProgressIndicator();
        VBox loadingBox = new VBox(20, loadingLabel, indicator);
        loadingBox.setAlignment(Pos.CENTER);
        root.setCenter(loadingBox);

        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/stars/" + UserSession.getInstance().getUserId()))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        MovieResponse movieResponse = gson.fromJson(response.body(), MovieResponse.class);

                        VBox myStarsContent = buildMovieListUI("⭐ My Starred Movies ⭐", movieResponse);
                        lastMovieListView = myStarsContent;
                        root.setCenter(myStarsContent);
                    } else {
                        Label errorLabel = new Label("Could not fetch your starred movies. Server response: " + response.statusCode());
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

    private void showSuggestionsView() {
        if (!UserSession.getInstance().isLoggedIn()) {
            showLoginView();
            return;
        }

        Label loadingLabel = new Label("Fetching Your Movie Suggestions...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");
        ProgressIndicator indicator = new ProgressIndicator();
        VBox loadingBox = new VBox(20, loadingLabel, indicator);
        loadingBox.setAlignment(Pos.CENTER);
        root.setCenter(loadingBox);

        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/suggestions/" + UserSession.getInstance().getUserId()))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        MovieResponse movieResponse = gson.fromJson(response.body(), MovieResponse.class);

                        VBox suggestionsContent = buildMovieListUI("⭐ Suggested For You ⭐", movieResponse);
                        lastMovieListView = suggestionsContent;
                        root.setCenter(suggestionsContent);
                    } else {
                        Label errorLabel = new Label("Could not fetch your movie suggestions. Server response: " + response.statusCode());
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

    private void starMovie(Movie m) {
        new Thread(() -> {
            try {
                UserStar star = new UserStar();
                star.setTmdbId(m.getId());
                star.setTitle(m.getTitle());
                User user = new User();
                user.setId(UserSession.getInstance().getUserId());
                star.setUser(user);

                String jsonBody = gson.toJson(star);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/stars"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void unstarMovie(Long tmdbId) {
        new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/stars/" + UserSession.getInstance().getUserId() + "/" + tmdbId))
                        .DELETE()
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void showQuizSelectionView() {
        Label title = new Label("Choose Quiz Type");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");


        Button generalBtn = new Button("🌍 General Knowledge");
        generalBtn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand; -fx-padding: 15 30; -fx-background-radius: 10;");
        makeButtonAnimated(generalBtn, false);
        generalBtn.setOnAction(e -> startQuizSession(false)); // false = not personalized


        Button personalBtn = new Button("⭐ My Favorites Quiz");
        personalBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 15 30; -fx-background-radius: 10;");
        makeButtonAnimated(personalBtn, true);


        if (UserSession.getInstance().isLoggedIn()) {
            personalBtn.setDisable(true);
            personalBtn.setText("⭐ My Favorites Quiz (Checking...)");
            personalBtn.setStyle("-fx-background-color: #555; -fx-text-fill: #aaa; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 10;");

            new Thread(() -> {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/v1/stars/" + UserSession.getInstance().getUserId()))
                            .header("Content-Type", "application/json")
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            MovieResponse movieResponse = gson.fromJson(response.body(), MovieResponse.class);
                            if (movieResponse != null && movieResponse.results != null && movieResponse.results.size() >= 3) {
                                personalBtn.setDisable(false);
                                personalBtn.setText("⭐ My Favorites Quiz");
                                personalBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 15 30; -fx-background-radius: 10;");
                                makeButtonAnimated(personalBtn, true);
                                personalBtn.setOnAction(e -> startQuizSession(true));
                            } else {
                                personalBtn.setText("⭐ My Favorites (Star at least 3 movies)");
                            }
                        } else {
                            personalBtn.setText("⭐ My Favorites (Error)");
                        }
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        personalBtn.setText("⭐ My Favorites (Connection Error)");
                    });
                }
            }).start();
        } else {
            personalBtn.setDisable(true);
            personalBtn.setText("⭐ My Favorites (Login Required)");
            personalBtn.setStyle("-fx-background-color: #555; -fx-text-fill: #aaa; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 10;");
        }

        VBox layout = new VBox(30, title, generalBtn, personalBtn);
        layout.setAlignment(Pos.CENTER);
        root.setCenter(layout);
    }

    private boolean isMovieStarred(Long tmdbId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/stars/" + UserSession.getInstance().getUserId() + "/" + tmdbId))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return Boolean.parseBoolean(response.body());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void showAIIntegrationView() {
        root.setTop(createHeader());

        // Title Section
        Label titleLabel = new Label("🎬 AI Actor Matcher");
        titleLabel.setStyle("-fx-text-fill: #E50914; -fx-font-size: 32px; -fx-font-weight: bold;");

        Label descriptionLabel = new Label("Upload your photo to find which celebrity you look like the most!");
        descriptionLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 16px;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(500);
        descriptionLabel.setTextAlignment(TextAlignment.CENTER);

        // Image Preview
        ImageView previewImageView = new ImageView();
        previewImageView.setFitWidth(200);
        previewImageView.setFitHeight(200);
        previewImageView.setPreserveRatio(true);
        previewImageView.setStyle("-fx-background-color: #333;");

        StackPane previewContainer = new StackPane(previewImageView);
        previewContainer.setStyle("-fx-background-color: #333; -fx-background-radius: 10; -fx-border-color: #555; -fx-border-radius: 10; -fx-border-width: 2;");
        previewContainer.setPrefSize(220, 220);
        previewContainer.setMaxSize(220, 220);

        Label placeholderLabel = new Label("No image selected");
        placeholderLabel.setStyle("-fx-text-fill: #888;");
        previewContainer.getChildren().add(placeholderLabel);

        // File selection
        final File[] selectedFile = {null};
        Label selectedFileLabel = new Label("");
        selectedFileLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        Button selectImageBtn = new Button("📁 Select Image");
        selectImageBtn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 10 20;");
        selectImageBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select an Image");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = fc.showOpenDialog(root.getScene().getWindow());
            if (file != null) {
                selectedFile[0] = file;
                selectedFileLabel.setText(file.getName());
                Image previewImage = new Image(file.toURI().toString(), 200, 200, true, true);
                previewImageView.setImage(previewImage);
                placeholderLabel.setVisible(false);
            }
        });

        // Results container
        VBox resultsContainer = new VBox(15);
        resultsContainer.setAlignment(Pos.CENTER);
        resultsContainer.setPadding(new Insets(20));
        resultsContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-background-radius: 10;");
        resultsContainer.setMaxWidth(800); // Increased container width
        resultsContainer.setVisible(false);

        // Find Similar Actor Button
        Button findActorBtn = new Button("🔍 Find Similar Actor");
        findActorBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 15 30;");
        makeButtonAnimated(findActorBtn, true);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(40, 40);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #cccccc;");

        findActorBtn.setOnAction(e -> {
            if (selectedFile[0] == null) {
                statusLabel.setStyle("-fx-text-fill: #E50914;");
                statusLabel.setText("Please select an image first!");
                return;
            }

            findActorBtn.setDisable(true);
            progressIndicator.setVisible(true);
            statusLabel.setStyle("-fx-text-fill: #cccccc;");
            statusLabel.setText("Analyzing image...");
            resultsContainer.setVisible(false);

            new Thread(() -> {
                try {
                    // Build multipart request
                    String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
                    String lineFeed = "\r\n";

                    StringBuilder multipartBody = new StringBuilder();

                    // Add userId field
                    multipartBody.append("--").append(boundary).append(lineFeed);
                    multipartBody.append("Content-Disposition: form-data; name=\"userId\"").append(lineFeed);
                    multipartBody.append(lineFeed);
                    multipartBody.append(UserSession.getInstance().getUserId()).append(lineFeed);

                    // Add file field header
                    multipartBody.append("--").append(boundary).append(lineFeed);
                    multipartBody.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(selectedFile[0].getName()).append("\"").append(lineFeed);
                    multipartBody.append("Content-Type: application/octet-stream").append(lineFeed);
                    multipartBody.append(lineFeed);

                    byte[] headerBytes = multipartBody.toString().getBytes(StandardCharsets.UTF_8);
                    byte[] fileBytes = Files.readAllBytes(selectedFile[0].toPath());
                    byte[] footerBytes = (lineFeed + "--" + boundary + "--" + lineFeed).getBytes(StandardCharsets.UTF_8);

                    byte[] requestBody = new byte[headerBytes.length + fileBytes.length + footerBytes.length];
                    System.arraycopy(headerBytes, 0, requestBody, 0, headerBytes.length);
                    System.arraycopy(fileBytes, 0, requestBody, headerBytes.length, fileBytes.length);
                    System.arraycopy(footerBytes, 0, requestBody, headerBytes.length + fileBytes.length, footerBytes.length);

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/ai/actor-similarity"))
                            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                            .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        findActorBtn.setDisable(false);

                        if (response.statusCode() == 200) {
                            statusLabel.setText("");
                            resultsContainer.getChildren().clear();

                            Label resultsTitle = new Label("🎭 Actor Similarity Results");
                            resultsTitle.setStyle("-fx-text-fill: #E50914; -fx-font-size: 20px; -fx-font-weight: bold;");
                            resultsContainer.getChildren().add(resultsTitle);

                            // Display string result directly
                            Label resultLabel = new Label(response.body());
                            resultLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                            resultLabel.setWrapText(true);
                            resultLabel.setTextAlignment(TextAlignment.CENTER);
                            resultLabel.setAlignment(Pos.CENTER); // Ensure label content is centered
                            resultLabel.setMaxWidth(750); // Increased width
                            
                            resultsContainer.getChildren().add(resultLabel);
                            resultsContainer.setVisible(true);
                        } else {
                            statusLabel.setStyle("-fx-text-fill: #E50914;");
                            statusLabel.setText("Error: " + response.body());
                        }
                    });

                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        findActorBtn.setDisable(false);
                        statusLabel.setStyle("-fx-text-fill: #E50914;");
                        statusLabel.setText("Connection error: " + ex.getMessage());
                    });
                }
            }).start();
        });

        // Layout
        VBox uploadSection = new VBox(15, previewContainer, selectImageBtn, selectedFileLabel);
        uploadSection.setAlignment(Pos.CENTER);

        VBox actionSection = new VBox(15, findActorBtn, progressIndicator, statusLabel);
        actionSection.setAlignment(Pos.CENTER);

        VBox mainContent = new VBox(30, titleLabel, descriptionLabel, uploadSection, actionSection, resultsContainer);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(40));

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);
    }




    public static void main(String[] args) {
        launch();
    }
}
