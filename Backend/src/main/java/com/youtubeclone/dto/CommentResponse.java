package com.youtubeclone.dto;

import java.time.LocalDateTime;


public class CommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UserInfo userProfile; // Simplified user info
    private Long parentCommentId; // ID of the parent comment, null for top-level
    private long likeCount; // Number of likes for this comment
    private boolean userLiked; // True if the current authenticated user liked this comment

    // Nested DTO for user information


    @Override
    public String toString() {
        return "CommentResponse{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", userProfile=" + userProfile +
                ", parentCommentId=" + parentCommentId +
                ", likeCount=" + likeCount +
                ", userLiked=" + userLiked +
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

    public UserInfo getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserInfo userProfile) {
        this.userProfile = userProfile;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isUserLiked() {
        return userLiked;
    }

    public void setUserLiked(boolean userLiked) {
        this.userLiked = userLiked;
    }

    public CommentResponse() {
    }

    public CommentResponse(Long id, String content, LocalDateTime createdAt, UserInfo userProfile, Long parentCommentId, long likeCount, boolean userLiked) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.userProfile = userProfile;
        this.parentCommentId = parentCommentId;
        this.likeCount = likeCount;
        this.userLiked = userLiked;
    }

    public static class UserInfo {
        private Long id;
        private String email;
        private ChannelInfo youtubeChannel; // Channel associated with the user

        @Override
        public String toString() {
            return "UserInfo{" +
                    "id=" + id +
                    ", email='" + email + '\'' +
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

        public ChannelInfo getYoutubeChannel() {
            return youtubeChannel;
        }

        public void setYoutubeChannel(ChannelInfo youtubeChannel) {
            this.youtubeChannel = youtubeChannel;
        }

        public UserInfo() {
        }

        public UserInfo(Long id, String email, ChannelInfo youtubeChannel) {
            this.id = id;
            this.email = email;
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
}
