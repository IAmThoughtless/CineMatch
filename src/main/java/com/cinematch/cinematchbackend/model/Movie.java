package com.cinematch.cinematchbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
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

    @JsonProperty("budget")
    private long budget;

    @JsonProperty("revenue")
    private long revenue;

    @JsonProperty("runtime")
    private int runtime;

    @JsonProperty("credits")
    private CreditsContainer credits;

    @JsonProperty("reviews")
    private ReviewsContainer reviews;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreditsContainer {
        @JsonProperty("cast")
        private List<CastMember> cast;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CastMember {
        @JsonProperty("name")
        private String name;

        @JsonProperty("character")
        private String character;

        @JsonProperty("profile_path")
        private String profilePath;
    }

    @Data
    public static class ReviewsContainer {
        @JsonProperty("results")
        private List<Review> results;
    }

}