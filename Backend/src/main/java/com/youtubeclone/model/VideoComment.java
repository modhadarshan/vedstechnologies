package com.youtubeclone.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "video_comment")
public class VideoComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id") // For replies
    private VideoComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoComment> replies = new HashSet<>();

    // Relationship to comment likes
    @OneToMany(mappedBy = "videoComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentLike> likes = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "VideoComment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", video=" + video +
                ", userProfile=" + userProfile +
                ", parentComment=" + parentComment +
                ", replies=" + replies +
                ", likes=" + likes +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public VideoComment getParentComment() {
        return parentComment;
    }

    public void setParentComment(VideoComment parentComment) {
        this.parentComment = parentComment;
    }

    public Set<VideoComment> getReplies() {
        return replies;
    }

    public void setReplies(Set<VideoComment> replies) {
        this.replies = replies;
    }

    public Set<CommentLike> getLikes() {
        return likes;
    }

    public void setLikes(Set<CommentLike> likes) {
        this.likes = likes;
    }

    public VideoComment() {
    }

    public VideoComment(Long id, String content, LocalDateTime createdAt, Video video, UserProfile userProfile, VideoComment parentComment, Set<VideoComment> replies, Set<CommentLike> likes) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.video = video;
        this.userProfile = userProfile;
        this.parentComment = parentComment;
        this.replies = replies;
        this.likes = likes;
    }
}