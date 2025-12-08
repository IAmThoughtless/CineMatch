package com.cinematch.cinematchbackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class Movie {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("poster_path")
    private String poster_path;

    @JsonProperty("backdrop_path")
    private String backdrop_path;

    @JsonProperty("release_date")
    private String release_date;

    @JsonProperty("popularity")
    private double popularity;

    @JsonProperty("vote_average")
    private double vote_average;

    @JsonProperty("vote_count")
    private int vote_count;

    @JsonProperty("reviews")
    private ReviewsContainer reviews;

    @Data
    public static class ReviewsContainer {
        @JsonProperty("results")
        private List<Review> results;
    }
}