package com.cinematch.cinematchbackend.model;

import com.example.cinematch.Movie;

import java.util.List;

public class MovieResponse {
    public int page;
    public List<Movie> results;
    public int total_pages;
    public int total_results;
}
