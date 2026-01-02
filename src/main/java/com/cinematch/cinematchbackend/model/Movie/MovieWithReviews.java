package com.cinematch.cinematchbackend.model.Movie;

import com.cinematch.cinematchbackend.model.Comments_Reviews.UserReview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieWithReviews {
    @com.google.gson.annotations.SerializedName("movie")
    private Movie movie;

    @com.google.gson.annotations.SerializedName("userReviews")
    private List<UserReview> userReviews;
}
