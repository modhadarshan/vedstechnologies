package com.youtubeclone.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "video_like")
public class VideoLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @Column(nullable = false)
    private LocalDateTime likedAt;

    @PrePersist
    protected void onCreate() {
        likedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "VideoLike{" +
                "id=" + id +
                ", video=" + video +
                ", userProfile=" + userProfile +
                ", likedAt=" + likedAt +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public LocalDateTime getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(LocalDateTime likedAt) {
        this.likedAt = likedAt;
    }

    public VideoLike() {
    }

    public VideoLike(Long id, Video video, UserProfile userProfile, LocalDateTime likedAt) {
        this.id = id;
        this.video = video;
        this.userProfile = userProfile;
        this.likedAt = likedAt;
    }
}