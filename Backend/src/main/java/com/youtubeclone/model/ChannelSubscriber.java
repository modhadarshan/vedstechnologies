package com.youtubeclone.model;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "channel_subscriber")
public class ChannelSubscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_channel_id", nullable = false)
    private YoutubeChannel subscriberChannel; // The channel that is subscribing

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscribed_to_channel_id", nullable = false)
    private YoutubeChannel subscribedToChannel; // The channel being subscribed to

    @Column(nullable = false)
    private LocalDateTime subscribedAt;

    @PrePersist
    protected void onCreate() {
        subscribedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ChannelSubscriber{" +
                "id=" + id +
                ", subscriberChannel=" + subscriberChannel +
                ", subscribedToChannel=" + subscribedToChannel +
                ", subscribedAt=" + subscribedAt +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public YoutubeChannel getSubscriberChannel() {
        return subscriberChannel;
    }

    public void setSubscriberChannel(YoutubeChannel subscriberChannel) {
        this.subscriberChannel = subscriberChannel;
    }

    public YoutubeChannel getSubscribedToChannel() {
        return subscribedToChannel;
    }

    public void setSubscribedToChannel(YoutubeChannel subscribedToChannel) {
        this.subscribedToChannel = subscribedToChannel;
    }

    public LocalDateTime getSubscribedAt() {
        return subscribedAt;
    }

    public void setSubscribedAt(LocalDateTime subscribedAt) {
        this.subscribedAt = subscribedAt;
    }

    public ChannelSubscriber() {
    }

    public ChannelSubscriber(Long id, YoutubeChannel subscriberChannel, YoutubeChannel subscribedToChannel, LocalDateTime subscribedAt) {
        this.id = id;
        this.subscriberChannel = subscriberChannel;
        this.subscribedToChannel = subscribedToChannel;
        this.subscribedAt = subscribedAt;
    }
}