  package com.IAmThoughtless.cinematch.dto;

public class SuggestionDTO {

    private Long id;
    private String title;
    private String type;
    private String year;

    // Constructors (απαραίτητοι για το Jackson JSON parsing)
    public SuggestionDTO(Long id, String title, String type, String year) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.year = year;
    }
    public SuggestionDTO() {}

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getYear() { return year; }
}