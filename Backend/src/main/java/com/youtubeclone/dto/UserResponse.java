package com.youtubeclone.dto;

import java.time.LocalDateTime;


public class UserResponse {
    private Long id;
    private String email;
    private LocalDateTime createdAt;
    private ChannelResponse youtubeChannel; // Include channel info

    // Nested class for channel response to avoid circular dependency and simplify DTO

    public UserResponse() {
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", youtubeChannel=" + youtubeChannel +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ChannelResponse getYoutubeChannel() {
        return youtubeChannel;
    }

    public void setYoutubeChannel(ChannelResponse youtubeChannel) {
        this.youtubeChannel = youtubeChannel;
    }

    public UserResponse(Long id, String email, LocalDateTime createdAt, ChannelResponse youtubeChannel) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
        this.youtubeChannel = youtubeChannel;
    }

    public static class ChannelResponse {
        private Long id;
        private String name;
        private String description;

        @Override
        public String toString() {
            return "ChannelResponse{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ChannelResponse() {
        }

        public ChannelResponse(Long id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
    }
}