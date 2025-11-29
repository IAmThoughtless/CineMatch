package com.example.cinematch;

public class MovieSearchPayload {
    private String query;

    public MovieSearchPayload(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
