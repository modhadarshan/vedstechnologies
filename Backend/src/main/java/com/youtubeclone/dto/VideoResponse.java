package com.youtubeclone.dto;

import java.time.LocalDateTime;
import java.util.Arrays;

public class VideoResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private LocalDateTime createdAt;
    private Long viewCount;
    private byte[] thumbnailData; // Thumbnail data (can be rendered as image)
    private ChannelInfo youtubeChannel; // Simplified channel info

    // Nested DTO for channel information


    @Override
    public String toString() {
        return "VideoResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                ", viewCount=" + viewCount +
                ", thumbnailData=" + Arrays.toString(thumbnailData) +
                ", youtubeChannel=" + youtubeChannel +
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

    public byte[] getThumbnailData() {
        return thumbnailData;
    }

    public void setThumbnailData(byte[] thumbnailData) {
        this.thumbnailData = thumbnailData;
    }

    public ChannelInfo getYoutubeChannel() {
        return youtubeChannel;
    }

    public void setYoutubeChannel(ChannelInfo youtubeChannel) {
        this.youtubeChannel = youtubeChannel;
    }

    public VideoResponse() {
    }

    public VideoResponse(Long id, String title, String description, String category, LocalDateTime createdAt, Long viewCount, byte[] thumbnailData, ChannelInfo youtubeChannel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.thumbnailData = thumbnailData;
        this.youtubeChannel = youtubeChannel;
    }

    public static class ChannelInfo {
        private Long id;
        private String name;

        @Override
        public String toString() {
            return "ChannelInfo{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ChannelInfo() {
        }

        public ChannelInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}