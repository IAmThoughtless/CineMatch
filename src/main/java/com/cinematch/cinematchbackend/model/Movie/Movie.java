package com.cinematch.cinematchbackend.model.Movie;

import com.cinematch.cinematchbackend.model.Comments_Reviews.Review;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonProperty("budget")
    private long budget;

    @JsonProperty("revenue")
    private long revenue;

    @JsonProperty("runtime")
    private int runtime;

    @JsonProperty("cast")
    private List<CastMember> cast;

    @JsonProperty("trailer_key")
    @com.google.gson.annotations.SerializedName("trailer_key")
    private String trailerKey;

    @Data
    public static class ReviewsContainer {
        @JsonProperty("results")
        private List<Review> results;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CastMember {
        @JsonProperty("id")
        @com.google.gson.annotations.SerializedName("id")
        private int id;

        @JsonProperty("name")
        @com.google.gson.annotations.SerializedName("name")
        private String name;

        @JsonProperty("character")
        @com.google.gson.annotations.SerializedName("character")
        private String character;

        @JsonProperty("profile_path")
        @com.google.gson.annotations.SerializedName("profile_path")
        private String profilePath;

    }

}