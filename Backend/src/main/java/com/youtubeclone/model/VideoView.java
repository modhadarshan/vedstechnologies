package com.youtubeclone.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_view")
public class VideoView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id") // Can be null for guest views
    private UserProfile userProfile;

    @Column(nullable = false)
    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreate() {
        viewedAt = LocalDateTime.now();
    }

    public VideoView() {
    }

    @Override
    public String toString() {
        return "VideoView{" +
                "id=" + id +
                ", video=" + video +
                ", userProfile=" + userProfile +
                ", viewedAt=" + viewedAt +
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

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }

    public VideoView(Long id, Video video, UserProfile userProfile, LocalDateTime viewedAt) {
        this.id = id;
        this.video = video;
        this.userProfile = userProfile;
        this.viewedAt = viewedAt;
    }
}
