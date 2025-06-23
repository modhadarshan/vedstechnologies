package com.youtubeclone.repository;

import com.youtubeclone.model.ChannelSubscriber;
import com.youtubeclone.model.YoutubeChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelSubscriberRepository extends JpaRepository<ChannelSubscriber, Long> {
    Optional<ChannelSubscriber> findBySubscriberChannelAndSubscribedToChannel(YoutubeChannel subscriberChannel, YoutubeChannel subscribedToChannel);
    long countBySubscribedToChannel(YoutubeChannel subscribedToChannel);
}