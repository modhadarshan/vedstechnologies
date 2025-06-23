package com.youtubeclone.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "video")

public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    @Lob // Large Object for binary data
    @Column(name = "video_data", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] videoData;

    @Lob // For thumbnail image (optional but good for UI)
    @Column(name = "thumbnail_data", columnDefinition = "MEDIUMBLOB")
    private byte[] thumbnailData;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Long viewCount = 0L; // Default view count

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "youtube_channel_id", nullable = false)
    private YoutubeChannel youtubeChannel;

    // Relationship to video views
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoView> views = new HashSet<>();

    // Relationship to video likes
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoLike> likes = new HashSet<>();

    // Relationship to video comments
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoComment> comments = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (viewCount == null) {
            viewCount = 0L;
        }
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", videoData=" + Arrays.toString(videoData) +
                ", thumbnailData=" + Arrays.toString(thumbnailData) +
                ", createdAt=" + createdAt +
                ", viewCount=" + viewCount +
                ", youtubeChannel=" + youtubeChannel +
                ", views=" + views +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public byte[] getVideoData() {
        return videoData;
    }

    public void setVideoData(byte[] videoData) {
        this.videoData = videoData;
    }

    public byte[] getThumbnailData() {
        return thumbnailData;
    }

    public void setThumbnailData(byte[] thumbnailData) {
        this.thumbnailData = thumbnailData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public YoutubeChannel getYoutubeChannel() {
        return youtubeChannel;
    }

    public void setYoutubeChannel(YoutubeChannel youtubeChannel) {
        this.youtubeChannel = youtubeChannel;
    }

    public Set<VideoView> getViews() {
        return views;
    }

    public void setViews(Set<VideoView> views) {
        this.views = views;
    }

    public Set<VideoLike> getLikes() {
        return likes;
    }

    public void setLikes(Set<VideoLike> likes) {
        this.likes = likes;
    }

    public Set<VideoComment> getComments() {
        return comments;
    }

    public void setComments(Set<VideoComment> comments) {
        this.comments = comments;
    }

    public Video() {
    }

    public Video(Long id, String title, String description, String category, byte[] videoData, byte[] thumbnailData, LocalDateTime createdAt, Long viewCount, YoutubeChannel youtubeChannel, Set<VideoView> views, Set<VideoLike> likes, Set<VideoComment> comments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.videoData = videoData;
        this.thumbnailData = thumbnailData;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.youtubeChannel = youtubeChannel;
        this.views = views;
        this.likes = likes;
        this.comments = comments;
    }
}