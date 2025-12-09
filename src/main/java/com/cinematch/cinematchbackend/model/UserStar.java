package com.cinematch.cinematchbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date; // Import Date

@Entity
@Table(name = "user_stars")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "tmdb_id", nullable = false)
    private Long tmdbId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "created_at", updatable = false)
    private Date createdAt;
}
