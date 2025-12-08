package com.cinematch.cinematchbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStarDTO {
    private Long id;
    private Long tmdbId;
    private String title;
    private String posterPath;
    private Long userId;

    public UserStarDTO(UserStar userStar) {
        this.id = userStar.getId();
        this.tmdbId = userStar.getTmdbId();
        this.title = userStar.getTitle();
        if (userStar.getUser() != null) {
            this.userId = userStar.getUser().getId();
        }
    }
}
