package com.cinematch.cinematchbackend.model.Quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LeaderboardDTO {
    private Long userId;
    private String username;
    private Integer score;
    private String createdAt;
}
