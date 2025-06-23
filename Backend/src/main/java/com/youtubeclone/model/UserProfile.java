package com.youtubeclone.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash; // Store hashed password

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // One-to-one relationship with YoutubeChannel
    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private YoutubeChannel youtubeChannel;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public YoutubeChannel getYoutubeChannel() {
        return youtubeChannel;
    }

    public void setYoutubeChannel(YoutubeChannel youtubeChannel) {
        this.youtubeChannel = youtubeChannel;
    }

    public UserProfile() {
    }

    public UserProfile(Long id, String email, String passwordHash, LocalDateTime createdAt, YoutubeChannel youtubeChannel) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.youtubeChannel = youtubeChannel;
    }
}
