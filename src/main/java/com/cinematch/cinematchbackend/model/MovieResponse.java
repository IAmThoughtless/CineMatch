package com.cinematch.cinematchbackend.model;

import com.example.cinematch.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieResponse {
    @JsonProperty("page")
    public int page;
    @JsonProperty("results")
    public List<Movie> results;
    @JsonProperty("total_pages")
    public int total_pages;
    @JsonProperty("total_results")
    public int total_results;

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
