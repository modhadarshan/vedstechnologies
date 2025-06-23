// dto/CommentRequest.java
// DTO for adding a comment
package com.youtubeclone.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequest {
    @NotBlank(message = "Comment content cannot be empty")
    private String content;
    private Long parentCommentId; // Optional, for replies

    public CommentRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public CommentRequest(String content, Long parentCommentId) {
        this.content = content;
        this.parentCommentId = parentCommentId;
    }

    @Override
    public String toString() {
        return "CommentRequest{" +
                "content='" + content + '\'' +
                ", parentCommentId=" + parentCommentId +
                '}';
    }
}