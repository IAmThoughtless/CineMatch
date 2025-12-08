package com.example.cinematch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("title")
    public String title;
    @JsonProperty("overview")
    public String overview;
    @JsonProperty("poster_path")
    public String poster_path;
    @JsonProperty("backdrop_path")
    public String backdrop_path;
    @JsonProperty("release_date")
    public String release_date;
    @JsonProperty("popularity")
    public double popularity;
    @JsonProperty("vote_average")
    public double vote_average;
    @JsonProperty("vote_count")
    public int vote_count;
}
