package com.cinematch.cinematchbackend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Review {
    @JsonProperty("author")
    private String author;

    @JsonProperty("content")
    private String content;

    @JsonProperty("id")
    private String id;

    // Manually added getters to resolve compilation errors if Lombok is not fully configured
    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}