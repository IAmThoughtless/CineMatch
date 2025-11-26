package com.IAmThoughtless.cinematch; // 🔑 ΔΙΟΡΘΩΘΗΚΕ: Η Κύρια Κλάση ΠΡΕΠΕΙ ΝΑ ΕΙΝΑΙ ΣΤΟ ΒΑΣΙΚΟ ΠΑΚΕΤΟ

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

// ΝΕΑ IMPORTS ΓΙΑ ΤΗ ΛΕΙΤΟΥΡΓΙΑ AUTO-SUGGEST
import javafx.concurrent.Task;
import java.util.List;
import javafx.util.Callback; // Προσθήκη για το ListCell
import com.IAmThoughtless.cinematch.service.SearchService;
import com.IAmThoughtless.cinematch.dto.SuggestionDTO;

public class ApplicationSearchBarCineMatch extends Application { // ΟΝΟΜΑ ΚΛΑΣΗΣ

    private BorderPane root;
    private final SearchService searchService = new SearchService(); // ΔΗΜΙΟΥΡΓΙΑ CLIENT API

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
        makeButtonAnimated(homeBtn, false);

        Button top10Btn = new Button("Top 10");
        top10Btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        makeButtonAnimated(top10Btn, false );

        Button loginBtn = new Button("Login / Register");
        loginBtn.setOnAction(event -> {showLoginView();});
        loginBtn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5;");
        makeButtonAnimated(loginBtn, true);

        Button QuizBtn = new Button("Quiz");
        QuizBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; ");
        makeButtonAnimated(QuizBtn, false);

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

        showHomeView();
    }

    // Η ΕΝΗΜΕΡΩΜΕΝΗ showHomeView()
    private void showHomeView() {
        Label welcomeLabel = new Label("Welcome to CineMatch");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        Label subTitle = new Label("Search for your favourite movie or actor/actress");
        subTitle.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 18px;");

        // ΑΛΛΑΓΗ: Χρήση ComboBox για τη λειτουργία Auto-Suggest
        ComboBox<SuggestionDTO> searchBar = new ComboBox<>();
        searchBar.setPromptText("Search...");
        searchBar.setEditable(true);
        searchBar.setPrefSize(300, 40);
        searchBar.getEditor().setStyle("-fx-background-color: white; -fx-font-size: 14px; -fx-background-radius: 20 0 0 20; -fx-padding: 0 15;");

        HBox searchBox = getHBox(searchBar);
        VBox.setMargin(searchBox, new Insets(30, 0, 0, 0));

        // --- ΝΕΟ: Ενσωμάτωση searchBox ---
        VBox searchArea = new VBox(0, searchBox);
        searchArea.setAlignment(Pos.CENTER);
        // -----------------------------------------------------------


        // --- ΝΕΟ: Cell Factory για σωστή εμφάνιση των Suggestions ---
        searchBar.setCellFactory(new Callback<ListView<SuggestionDTO>, ListCell<SuggestionDTO>>() {
            @Override
            public ListCell<SuggestionDTO> call(ListView<SuggestionDTO> param) {
                return new ListCell<SuggestionDTO>() {
                    @Override
                    protected void updateItem(SuggestionDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            // Εμφάνιση Τίτλου, Έτους και Τύπου
                            setText(item.getTitle() + (item.getYear() != null ? " (" + item.getYear() + ")" : "") + (item.getType() != null ? " - " + item.getType() : ""));
                        }
                    }
                };
            }
        });

        // --- ΝΕΟ: Listener για το Auto-Suggest (στο πεδίο επεξεργασίας του ComboBox) ---
        searchBar.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 3 && !searchBar.isShowing()) {
                // Ασύγχρονη Task για να καλέσει το Backend API
                Task<List<SuggestionDTO>> task = new Task<>() {
                    @Override
                    protected List<SuggestionDTO> call() throws Exception {
                        // Καλούμε τον SearchService για να φέρει τα δεδομένα
                        return searchService.fetchSuggestionsFromApi(newValue);
                    }
                };

                task.setOnSucceeded(e -> {
                    // Ενημέρωση του UI με τα αποτελέσματα
                    List<SuggestionDTO> suggestions = task.getValue();
                    searchBar.getItems().setAll(suggestions); // Ενημέρωση της λίστας
                    if (!suggestions.isEmpty()) {
                        searchBar.show(); // Εμφάνιση του dropdown
                    } else {
                        searchBar.hide();
                    }
                });

                task.setOnFailed(e -> {
                    System.err.println("API Call failed: " + task.getException());
                    searchBar.hide();
                });

                new Thread(task).start(); // Εκκίνηση του ασύγχρονου νήματος

            } else if (newValue.length() < 3) {
                // Αν το κείμενο είναι πολύ μικρό, κρύβουμε τις προτάσεις
                searchBar.hide();
                searchBar.getItems().clear();
            }
        });
        // ---------------------------------------------


        // --- ΑΛΛΑΓΗ: Χρήση του νέου searchArea ---
        VBox homeContent = new VBox(10, welcomeLabel, subTitle, searchArea);
        homeContent.setAlignment(Pos.CENTER);
        // ------------------------------------------

        root.setCenter(homeContent);
    }

    private static HBox getHBox(ComboBox<SuggestionDTO> searchBar) {
        Button searchButton = new Button("Search");
        searchButton.setPrefSize(120, 40);
        searchButton.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 0 20 20 0;");


        // --- Διόρθωση: Ο ComboBox διαχειρίζεται το dropdown από μόνος του. Αφαιρούμε το VBox suggestionsContainer ---
        // VBox suggestionsContainer = new VBox();
        // suggestionsContainer.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-padding: 5;");
        // suggestionsContainer.setMaxWidth(searchField.getPrefWidth() + searchButton.getPrefWidth());
        // suggestionsContainer.setVisible(false);
        // --------------------------------------------------

        HBox searchBox = new HBox(0, searchBar, searchButton); // Αλλαγή: Χρήση searchBar
        searchBox.setAlignment(Pos.CENTER);
        return searchBox;
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
        makeButtonAnimated(signInBtn, true);

        Label registerLink = new Label("New to CineMatch? Sign up now.");
        registerLink.setStyle("-fx-text-fill: #cccccc; -fx-cursor: hand;");
        registerLink.setOnMouseClicked( event -> {showRegisterView();});
        registerLink.setOnMouseEntered(e -> registerLink.setStyle("-fx-text-fill: white; -fx-underline: true;"));
        registerLink.setOnMouseExited(e -> registerLink.setStyle("-fx-text-fill: #cccccc; -fx-underline: false;"));

        VBox loginForm = new VBox(20, loginTitle, usernameField, passwordField, signInBtn, registerLink);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setPadding(new Insets(20, 20, 20, 20));
        loginForm.setMaxWidth(400);
        loginForm.setMaxHeight(300);
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

        Label loginLink = new Label("Already have an account? Sign in.");
        loginLink.setStyle("-fx-text-fill: #cccccc; -fx-cursor: hand;");
        loginLink.setOnMouseClicked(e -> showLoginView());
        loginLink.setOnMouseEntered(e -> loginLink.setStyle("-fx-text-fill: white; -fx-underline: true;"));
        loginLink.setOnMouseExited(e -> loginLink.setStyle("-fx-text-fill: #cccccc; -fx-underline: false;"));

        VBox regForm = new VBox(20, regTitle, emailField, userField, passField, confirmPassField, registerBtn, loginLink);
        regForm.setAlignment(Pos.CENTER);
        regForm.setPadding(new Insets(40));
        regForm.setMaxWidth(400);
        regForm.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-background-radius: 10;");

        root.setCenter(regForm);
    }

    // ΑΦΑΙΡΕΣΗ ΤΗΣ ΜΕΘΟΔΟΥ updateSuggestionsUI: Η λειτουργία μεταφέρθηκε στο ComboBox
    // (Δεν χρειάζεται πλέον αυτή η μέθοδος)

    private void makeButtonAnimated(Button btn, boolean isRedButton) {

        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.10);
            btn.setScaleY(1.10);
            if (isRedButton) {
                // Ελέγχουμε αν υπάρχει fill πριν πάρουμε το radius.
                double radius = btn.getBackground() != null && !btn.getBackground().getFills().isEmpty() ?
                        btn.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius() : 5; // default 5
                btn.setStyle("-fx-background-color: #ff1f2c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: " + radius + ";");
            }
        });


        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            if (isRedButton) {
                // Ελέγχουμε αν υπάρχει fill πριν πάρουμε το radius.
                double radius = btn.getBackground() != null && !btn.getBackground().getFills().isEmpty() ?
                        btn.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius() : 5; // default 5
                btn.setStyle("-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: " + radius + ";");
            }
        });
    }

    // ΠΡΟΣΘΗΚΗ: Η κύρια μέθοδος main για την εκκίνηση της JavaFX
    public static void main(String[] args) {
        launch(args);
    }
}