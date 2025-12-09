package com.cinematch.cinematchbackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

        @Entity // 1. Δηλώνει ότι αυτή η κλάση αντιστοιχεί σε έναν πίνακα της βάσης
        @Table(name = "comments") // 2. Καθορίζει το όνομα του πίνακα στη βάση
        @Data
            public class Comment {

            @Id // 3. Ορίζει το πρωτεύον κλειδί
            @GeneratedValue(strategy = GenerationType.IDENTITY) // 4. Δίνει τιμή στο ID αυτόματα (auto-increment)
            private Long id;

    // 5. Πεδία που αντιστοιχούν στα δεδομένα του σχολίου
    @Column(name = "movie_id")
    private Long movieId;

            public Long getMovieId() {
                return movieId;
            }

            public void setMovieId(Long movieId) {
                this.movieId = movieId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public int getRating() {
                return rating;
            }

            public void setRating(int rating) {
                this.rating = rating;
            }

            public LocalDateTime getDateCreated() {
                return dateCreated;
            }

            public void setDateCreated(LocalDateTime dateCreated) {
                this.dateCreated = dateCreated;
            }

            @Column(name = "user_name")
    private String userName;

    @Column(name = "comment_text") // Το όνομα της στήλης στη βάση
    private String text; // Το όνομα του πεδίου στη Java

    private int rating; // Αν η στήλη στη βάση λέγεται 'rating'

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    // 6. Constructors (Ένας κενός απαιτείται από το JPA)
    public Comment() {
    }


            public Long getId() {
             return id;
            }

            public void setId(Long id) {
             this.id = id;
           }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}






