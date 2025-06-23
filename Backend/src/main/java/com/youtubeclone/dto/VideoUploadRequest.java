package com.youtubeclone.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public class VideoUploadRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private String category;
    // MultipartFile will be handled directly in the controller
    // @NotBlank // Cannot apply @NotBlank directly to MultipartFile for required check
    // private MultipartFile videoFile;
    // private MultipartFile thumbnailFile; // Optional

    @Override
    public String toString() {
        return "VideoUploadRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                '}';
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

    public VideoUploadRequest() {
    }

    public VideoUploadRequest(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }
}
