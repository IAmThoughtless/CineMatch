package com.cinematch.cinematchbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieWithReviews {
    private Movie movie;
    private List<UserReview> userReviews;
}
