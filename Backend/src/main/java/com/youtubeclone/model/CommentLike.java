package com.youtubeclone.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "comment_like")
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_comment_id", nullable = false)
    private VideoComment videoComment;

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
        return "CommentLike{" +
                "id=" + id +
                ", videoComment=" + videoComment +
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

    public VideoComment getVideoComment() {
        return videoComment;
    }

    public void setVideoComment(VideoComment videoComment) {
        this.videoComment = videoComment;
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

    public CommentLike() {
    }

    public CommentLike(Long id, VideoComment videoComment, UserProfile userProfile, LocalDateTime likedAt) {
        this.id = id;
        this.videoComment = videoComment;
        this.userProfile = userProfile;
        this.likedAt = likedAt;
    }
}
