package com.youtubeclone.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "youtube_channel")
public class YoutubeChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id", nullable = false)
    private UserProfile userProfile;

    // Videos associated with this channel
    @OneToMany(mappedBy = "youtubeChannel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Video> videos = new HashSet<>();

    // Subscriptions made by this channel (i.e., this channel is subscribing to others)
    // This side is for the 'subscriber' role
    @OneToMany(mappedBy = "subscriberChannel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChannelSubscriber> subscriptions = new HashSet<>();

    // Subscribers to this channel (i.e., other channels subscribing to this one)
    // This side is for the 'subscribedTo' role
    @OneToMany(mappedBy = "subscribedToChannel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChannelSubscriber> subscribers = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public YoutubeChannel() {
    }

    public YoutubeChannel(Long id, String name, String description, LocalDateTime createdAt, UserProfile userProfile, Set<Video> videos, Set<ChannelSubscriber> subscriptions, Set<ChannelSubscriber> subscribers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.userProfile = userProfile;
        this.videos = videos;
        this.subscriptions = subscriptions;
        this.subscribers = subscribers;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public Set<Video> getVideos() {
        return videos;
    }

    public void setVideos(Set<Video> videos) {
        this.videos = videos;
    }

    public Set<ChannelSubscriber> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<ChannelSubscriber> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Set<ChannelSubscriber> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<ChannelSubscriber> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String toString() {
        return "YoutubeChannel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", userProfile=" + userProfile +
                ", videos=" + videos +
                ", subscriptions=" + subscriptions +
                ", subscribers=" + subscribers +
                '}';
    }
}