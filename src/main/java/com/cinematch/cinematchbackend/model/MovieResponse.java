package com.cinematch.cinematchbackend.model;

import java.util.List;

public class MovieResponse {
    public int page;
    public List<Movie> results;
    public int total_pages;
    public int total_results;

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
