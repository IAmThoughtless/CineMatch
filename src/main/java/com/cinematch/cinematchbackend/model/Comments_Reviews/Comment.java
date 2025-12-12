package com.cinematch.cinematchbackend.model.Comments_Reviews;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

        @Entity
        @Table(name = "comments")
        @Data
            public class Comment {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

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

    @Column(name = "comment_text")
    private String text;

    private int rating;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

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






