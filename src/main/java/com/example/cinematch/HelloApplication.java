package com.example.cinematch;

// Import your Backend Models
// If your User class is in a different package, change the line above!

import com.cinematch.cinematchbackend.model.MovieResponse;
import com.cinematch.cinematchbackend.model.Movie;
import com.cinematch.cinematchbackend.model.Review;
import com.cinematch.cinematchbackend.model.User;
import com.cinematch.cinematchbackend.model.UserReview;
import com.cinematch.cinematchbackend.model.MovieWithReviews;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.Date;
import java.util.Map;


import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Map;
import java.util.List;


public class HelloApplication extends Application {
    private VBox whatsHotContainer;
    private VBox lastMovieListView;
    private BorderPane root;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private final int TOTAL_QUESTIONS = 5;
    private java.util.List<com.cinematch.cinematchbackend.model.QuizQuestion> loadedQuestions;
    private final Map<String, Integer> genreMap = Map.of(
            "Action", 28,
            "Comedy", 35,
            "Drama", 18,
            "Horror", 27,
            "Sci-Fi", 878
    );
    public MenuButton createGenreMenuButton() {
        MenuButton genresMenuButton = new MenuButton("GENRES");
        genresMenuButton.setPadding(new Insets(0));
        genresMenuButton.setStyle(
                        "-fx-text-fill: #E50914;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 10 0 0;");


        for (Map.Entry<String, Integer> entry : genreMap.entrySet()) {
            String genreName = entry.getKey();

            MenuItem item = new MenuItem(genreName);

            item.setOnAction(this::handleGenreSelection);

            genresMenuButton.getItems().add(item);
        }
        return genresMenuButton;
    }

    private void handleGenreSelection(ActionEvent event) {

        MenuItem source = (MenuItem) event.getSource();
        String selectedGenreName = source.getText();


        int genreId = genreMap.get(selectedGenreName);

        System.out.println("Επιλέχθηκε κατηγορία: " + selectedGenreName + ", ID: " + genreId);
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

        Scene scene = new Scene(root, 900, 650);
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
        VBox sidebarContainer = new VBox(10);
        sidebarContainer.setPadding(new Insets(20));
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
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/genre/" + genreId))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    targetContainer.getChildren().clear();
                    Gson gson = new Gson();
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
        targetContainer.getChildren().add(loadingBox); // Εμφάνιση loading

        new Thread(() -> {
            try (HttpClient client = HttpClient.newHttpClient()) {
                // Κλήση στο νέο endpoint
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/whats-hot"))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    targetContainer.getChildren().clear(); // Αφαίρεση loading
                    Gson gson = new Gson();
                    MovieResponse movies = gson.fromJson(response.body(), MovieResponse.class);

                    if (response.statusCode() != 200 || movies == null || movies.results == null) {
                        Label errorLabel = new Label("Could not fetch What's Hot movies. Check API.");
                        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
                        targetContainer.getChildren().add(errorLabel);
                    }
                    else {
                        // Χρησιμοποιούμε τη νέα μέθοδο για εμφάνιση της λίστας
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
            try (HttpClient client = HttpClient.newHttpClient()) {
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

        signInBtn.setOnAction(event -> {
            signInBtn.setDisable(true);
            String username = usernameField.getText();
            String password = passwordField.getText();

            new Thread(() -> {
                try (HttpClient client = HttpClient.newHttpClient()) {
                    // Assuming User constructor: User(username, password)
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);

                    String jsonBody = new Gson().toJson(user);

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
                            Gson gson = new Gson();
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
                try (HttpClient client = HttpClient.newHttpClient()) {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    String jsonBody = new Gson().toJson(newUser);

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
        VBox.setMargin(titleLabel, new Insets(0, 0, 10, 50)); // Εμφάνιση τίτλου αριστερά

        // 1. Δημιουργία HBox για τις κάρτες ταινιών (Οριζόντια διάταξη)
        HBox movieRow = new HBox(20); // 20px κενό μεταξύ των καρτών
        movieRow.setPadding(new Insets(0, 50, 0, 50)); // Οριζόντιο padding (αριστερά/δεξιά)

        if (movieResponse == null || movieResponse.results == null || movieResponse.results.isEmpty()) {
            Label noResultsLabel = new Label("No movies found for this section.");
            noResultsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            // Αν δεν υπάρχουν αποτελέσματα, τοποθετούμε το μήνυμα σε VBox
            return new VBox(20, titleLabel, noResultsLabel);
        }

        // 2. Δημιουργία και προσθήκη καρτών
        int limit = Math.min(20, movieResponse.results.size()); // Αύξηση του ορίου σε 20 για οριζόντια σειρά

        for (int i = 0; i < limit; i++) {
            Movie m = movieResponse.results.get(i);

            // --- Δημιουργία μιας Κάρτας Ταινίας (Στοιχείο VBox) ---
            // Εδώ η κάρτα γίνεται VBox για να περιέχει την αφίσα και τον τίτλο κάθετα

            ImageView posterView = createPosterImageView(m.getPoster_path());
            posterView.setFitWidth(150); // Μεγαλύτερη αφίσα για οριζόντια σειρά
            posterView.setFitHeight(225);

            Label movieTitle = new Label(m.getTitle());
            movieTitle.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
            movieTitle.setWrapText(true);
            movieTitle.setMaxWidth(150);
            movieTitle.setMaxHeight(40); // Περιορισμός ύψους τίτλου

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

        // 3. Τύλιγμα της οριζόντιας σειράς σε ScrollPane
        ScrollPane horizontalScrollPane = new ScrollPane(movieRow);
        horizontalScrollPane.setFitToHeight(true);
        horizontalScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Να εμφανίζεται η μπάρα κύλισης
        horizontalScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Απενεργοποίηση κάθετης κύλισης
        horizontalScrollPane.setPrefHeight(350); // Καθορισμός ύψους για τη σειρά (αφίσα + τίτλος + padding)
        horizontalScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // 4. Επιστροφή του τελικού κατακόρυφου layout (Τίτλος + ScrollPane)
        VBox finalLayout = new VBox(10, titleLabel, horizontalScrollPane);
        finalLayout.setAlignment(Pos.TOP_LEFT);
        finalLayout.setMaxWidth(900);

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
            try (HttpClient client = HttpClient.newHttpClient()) {
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

            // 2. Show "My Stars" Button
            Button myStarsBtn = new Button("My Stars");
            myStarsBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            myStarsBtn.setOnAction(event -> showMyStarsView());
            makeButtonAnimated(myStarsBtn, false);

            // 3. Show Logout Button
            Button logoutBtn = new Button("Logout");
            logoutBtn.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-radius: 5; -fx-text-fill: white; -fx-cursor: hand;");
            logoutBtn.setOnAction(e -> {
                UserSession.getInstance().cleanUserSession(); // Clear session
                showHomeView(); // Refresh view
            });

            header.getChildren().addAll(welcomeUser, myStarsBtn, logoutBtn);

        } else {
            // 4. If NOT logged in, show Login Button
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
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/quiz/batch"))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        Gson gson = new Gson();
                        java.lang.reflect.Type listType = new TypeToken<java.util.List<com.cinematch.cinematchbackend.model.QuizQuestion>>(){}.getType();

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

    private void showMovieDetails(Movie initialMovieData) {
        // Κουμπί επιστροφής
        Button backBtn = new Button("⬅ Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #E50914; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if (lastMovieListView != null) {
                root.setCenter(lastMovieListView);
            } else {
                showHomeView();
            }
        });

        // --- Βασικά Στοιχεία //
        ImageView posterView = createPosterImageView(initialMovieData.getPoster_path());
        posterView.setFitWidth(300);
        posterView.setFitHeight(450);

        Label titleLabel = new Label(initialMovieData.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);

        String date = (initialMovieData.getRelease_date() != null && !initialMovieData.getRelease_date().isEmpty()) ? initialMovieData.getRelease_date() : "N/A";
        Label metaLabel = new Label("📅 " + date + "  |  ⭐ " + initialMovieData.getVote_average() + "/10 (" + initialMovieData.getVote_count() + " votes)");
        metaLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 16px;");

        String overviewText = (initialMovieData.getOverview() != null && !initialMovieData.getOverview().isEmpty()) ? initialMovieData.getOverview() : "Δεν υπάρχει διαθέσιμη περιγραφή.";
        Label overviewLabel = new Label(overviewText);
        overviewLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        overviewLabel.setWrapText(true);
        overviewLabel.setMaxWidth(600);

        // --- Κουμπί Star ---
        Button starBtn = new Button("Loading...");
        starBtn.setDisable(true);

        if (UserSession.getInstance().isLoggedIn()) {
            new Thread(() -> {
                boolean isStarred = isMovieStarred(initialMovieData.getId());
                Platform.runLater(() -> {
                    starBtn.setDisable(false);
                    if (isStarred) {
                        setupUnstarButton(starBtn, initialMovieData);
                    } else {
                        setupStarButton(starBtn, initialMovieData);
                    }
                });
            }).start();
        } else {
            starBtn.setText("⭐ Star Movie");
            starBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
            starBtn.setDisable(false);
            starBtn.setOnAction(ev -> showLoginView());
        }

        VBox infoBox = new VBox(20, titleLabel, metaLabel, overviewLabel, starBtn);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        HBox topContent = new HBox(40, posterView, infoBox);
        topContent.setAlignment(Pos.CENTER);
        topContent.setPadding(new Insets(0, 0, 40, 0));

        // --- ΤΜΗΜΑ ΚΡΙΤΙΚΩΝ (REVIEWS SECTION) ---
        VBox reviewsContainer = new VBox(15);
        reviewsContainer.setAlignment(Pos.TOP_LEFT);
        reviewsContainer.setMaxWidth(800);

        Label reviewsHeader = new Label("User Reviews");
        reviewsHeader.setStyle("-fx-text-fill: #E50914; -fx-font-size: 24px; -fx-font-weight: bold;");

        VBox userReviewsBox = new VBox(10);
        userReviewsBox.setAlignment(Pos.TOP_LEFT);

        Label loadingReviewsLabel = new Label("Loading reviews...");
        loadingReviewsLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");

        reviewsContainer.getChildren().addAll(reviewsHeader, userReviewsBox, loadingReviewsLabel);

        // --- Add Review Form ---
        if (UserSession.getInstance().isLoggedIn()) {
            TextArea reviewTextArea = new TextArea();
            reviewTextArea.setPromptText("Write your review here...");
            reviewTextArea.setWrapText(true);
            reviewTextArea.setPrefHeight(100);
            reviewTextArea.setStyle("-fx-control-inner-background:#333; -fx-prompt-text-fill: white; -fx-text-fill: white; -fx-background-radius: 5;");

            Button submitReviewBtn = new Button("Submit Review");
            submitReviewBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");

            new Thread(() -> {
                UserReview userReview = getUserReview(initialMovieData.getId());
                Platform.runLater(() -> {
                    if (userReview != null) {
                        reviewTextArea.setText(userReview.getReviewText());
                    }
                });
            }).start();

            submitReviewBtn.setOnAction(e -> {
                String reviewText = reviewTextArea.getText();
                if (reviewText != null && !reviewText.trim().isEmpty()) {
                    submitReview(initialMovieData, reviewText);
                }
            });
            VBox addReviewBox = new VBox(10, new Label("Add Your Review"), reviewTextArea, submitReviewBtn);
            addReviewBox.setAlignment(Pos.TOP_LEFT);
            reviewsContainer.getChildren().add(addReviewBox);
        } else {
            TextArea reviewTextArea = new TextArea();
            reviewTextArea.setPromptText("Login or register to write a review");
            reviewTextArea.setWrapText(true);
            reviewTextArea.setPrefHeight(100);
            reviewTextArea.setEditable(false); // Make it non-editable
            reviewTextArea.setStyle("-fx-control-inner-background:#333; -fx-prompt-text-fill: white; -fx-background-radius: 5;");
            reviewTextArea.setOnMouseClicked(e -> showLoginView()); // Redirect to login on click

            Button submitReviewBtn = new Button("Submit Review");
            submitReviewBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
            submitReviewBtn.setOnAction(e -> showLoginView()); // Redirect to login on button click

            VBox addReviewBox = new VBox(10, new Label("Add Your Review"), reviewTextArea, submitReviewBtn);
            addReviewBox.setAlignment(Pos.TOP_LEFT);
            reviewsContainer.getChildren().add(addReviewBox);
        }
        


        // ---  UI ---
        VBox mainContent = new VBox(20, topContent, reviewsContainer);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(40));

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPannable(true);

        VBox finalLayout = new VBox(20, backBtn, scrollPane);
        finalLayout.setPadding(new Insets(20));

        root.setCenter(finalLayout);


        new Thread(() -> {
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/movie/" + initialMovieData.getId()))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        Gson gson = new Gson();
                        MovieWithReviews movieWithReviews = gson.fromJson(response.body(), MovieWithReviews.class);
                        Movie fullMovie = movieWithReviews.getMovie();
                        java.util.List<UserReview> userReviews = movieWithReviews.getUserReviews();

                        reviewsContainer.getChildren().remove(loadingReviewsLabel);

                        // Display User Reviews
                        userReviewsBox.getChildren().clear();
                        if (userReviews != null && !userReviews.isEmpty()) {
                            for (UserReview review : userReviews) {
                                Label authorLabel = new Label("👤 " + review.getUser().getUsername());
                                authorLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-weight: bold; -fx-font-size: 14px;");

                                Label contentLabel = new Label(review.getReviewText());
                                contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                                contentLabel.setWrapText(true);
                                contentLabel.setMaxWidth(750);
                                
                                Label dateLabel = new Label("Created at: " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(review.getCreatedAt()));
                                dateLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

                                VBox reviewBox = new VBox(5, authorLabel, contentLabel, dateLabel);
                                reviewBox.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 15; -fx-background-radius: 10;");
                                userReviewsBox.getChildren().add(reviewBox);
                            }
                        } else {
                            Label noReviews = new Label("No user reviews yet.");
                            noReviews.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
                            userReviewsBox.getChildren().add(noReviews);
                        }


                        if (fullMovie.getReviews() != null && fullMovie.getReviews().getResults() != null && !fullMovie.getReviews().getResults().isEmpty()) {


                            for (Review review : fullMovie.getReviews().getResults()) {
                                Label authorLabel = new Label("👤 " + review.getAuthor());
                                authorLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-weight: bold; -fx-font-size: 14px;");

                                Label contentLabel = new Label();
                                contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                                contentLabel.setWrapText(true);
                                contentLabel.setMaxWidth(750); // Σιγουρέψου ότι αυτό χωράει στο UI σου

                                VBox reviewBox = new VBox(5);
                                reviewBox.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-padding: 15; -fx-background-radius: 10;");

                                // --- ΛΟΓΙΚΗ EXPAND  ---
                                int MAX_LENGTH = 400;

                                if (review.getContent().length() > MAX_LENGTH) {
                                    String fullText = review.getContent();
                                    String truncatedText = review.getContent().substring(0, MAX_LENGTH) + "...";


                                    contentLabel.setText(truncatedText);


                                    javafx.scene.control.Hyperlink expandLink = new javafx.scene.control.Hyperlink("Read More ⬇");
                                    expandLink.setStyle("-fx-text-fill: #E50914; -fx-border-color: transparent; -fx-font-weight: bold;");


                                    expandLink.setOnAction(e -> {
                                        if (expandLink.getText().equals("Read More ⬇")) {

                                            contentLabel.setText(fullText);
                                            expandLink.setText("Read Less ⬆");
                                        } else {

                                            contentLabel.setText(truncatedText);
                                            expandLink.setText("Read More ⬇");
                                        }
                                    });

                                    reviewBox.getChildren().addAll(authorLabel, contentLabel, expandLink);
                                } else {

                                    contentLabel.setText(review.getContent());
                                    reviewBox.getChildren().addAll(authorLabel, contentLabel);
                                }

                                reviewsContainer.getChildren().add(reviewBox);
                            }

                        } else {
                            Label noReviews = new Label("No reviews found for this movie.");
                            noReviews.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
                            reviewsContainer.getChildren().add(noReviews);
                        }
                    } else {
                        loadingReviewsLabel.setText("Failed to load reviews.");
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> loadingReviewsLabel.setText("Error loading reviews."));
            }
        }).start();
    }

    private void submitReview(Movie movie, String reviewText) {
        new Thread(() -> {
            try (HttpClient client = HttpClient.newHttpClient()) {
                UserReview review = new UserReview();
                review.setTmdbId(movie.getId());
                review.setReviewText(reviewText);
                User user = new User();
                user.setId(UserSession.getInstance().getUserId());
                review.setUser(user);

                String jsonBody = new Gson().toJson(review);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/reviews"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    Platform.runLater(() -> {
                        // Refresh the movie details view to show the new review
                        showMovieDetails(movie);
                    });
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private UserReview getUserReview(Long tmdbId) {
        if (!UserSession.getInstance().isLoggedIn()) {
            return null;
        }
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/reviews/" + UserSession.getInstance().getUserId() + "/" + tmdbId))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null && !response.body().isEmpty()) {
                return new Gson().fromJson(response.body(), UserReview.class);
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
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/v1/stars/" + UserSession.getInstance().getUserId()))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        Gson gson = new Gson();
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

    private void starMovie(Movie m) {
        new Thread(() -> {
            try (HttpClient client = HttpClient.newHttpClient()) {
                com.cinematch.cinematchbackend.model.UserStar star = new com.cinematch.cinematchbackend.model.UserStar();
                star.setTmdbId(m.getId());
                star.setTitle(m.getTitle());
                User user = new User();
                user.setId(UserSession.getInstance().getUserId());
                star.setUser(user);

                String jsonBody = new Gson().toJson(star);

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
            try (HttpClient client = HttpClient.newHttpClient()) {
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

    private boolean isMovieStarred(Long tmdbId) {
        try (HttpClient client = HttpClient.newHttpClient()) {
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

    public static void main(String[] args) {
        launch();
    }
}
