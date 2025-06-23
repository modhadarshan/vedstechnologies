package com.youtubeclone.service;

import com.youtubeclone.exception.ResourceNotFoundException;
import com.youtubeclone.model.ChannelSubscriber;
import com.youtubeclone.model.UserProfile;
import com.youtubeclone.model.YoutubeChannel;
import com.youtubeclone.repository.ChannelSubscriberRepository;
import com.youtubeclone.repository.YoutubeChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class YoutubeChannelService {

    @Autowired
    private YoutubeChannelRepository youtubeChannelRepository;

    @Autowired
    private ChannelSubscriberRepository channelSubscriberRepository;

    /**
     * Finds a channel by its ID.
     * @param channelId The ID of the channel.
     * @return The YoutubeChannel.
     * @throws ResourceNotFoundException if the channel is not found.
     */
    public YoutubeChannel getChannelById(Long channelId) {
        return youtubeChannelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + channelId));
    }

    /**
     * Finds a channel by its associated user profile.
     * @param userProfile The user profile.
     * @return The YoutubeChannel.
     * @throws ResourceNotFoundException if the channel is not found for the user.
     */
    public YoutubeChannel getChannelByUserProfile(UserProfile userProfile) {
        return youtubeChannelRepository.findByUserProfile(userProfile)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found for user: " + userProfile.getEmail()));
    }

    /**
     * Subscribes a user's channel to another channel.
     * @param subscriberUserProfile The user who is subscribing.
     * @param channelToSubscribeToId The ID of the channel to subscribe to.
     * @return The created ChannelSubscriber entity.
     * @throws ResourceNotFoundException if either channel is not found.
     * @throws IllegalArgumentException if a user tries to subscribe to their own channel.
     * @throws IllegalStateException if the user is already subscribed.
     */
    @Transactional
    public ChannelSubscriber subscribeToChannel(UserProfile subscriberUserProfile, Long channelToSubscribeToId) {
        YoutubeChannel subscriberChannel = youtubeChannelRepository.findByUserProfile(subscriberUserProfile)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber channel not found for user: " + subscriberUserProfile.getEmail()));
        YoutubeChannel subscribedToChannel = getChannelById(channelToSubscribeToId);

        if (subscriberChannel.getId().equals(subscribedToChannel.getId())) {
            throw new IllegalArgumentException("Cannot subscribe to your own channel.");
        }

        Optional<ChannelSubscriber> existingSubscription = channelSubscriberRepository.findBySubscriberChannelAndSubscribedToChannel(subscriberChannel, subscribedToChannel);
        if (existingSubscription.isPresent()) {
            throw new IllegalStateException("Already subscribed to this channel.");
        }

        ChannelSubscriber subscription = new ChannelSubscriber();
        subscription.setSubscriberChannel(subscriberChannel);
        subscription.setSubscribedToChannel(subscribedToChannel);
        return channelSubscriberRepository.save(subscription);
    }

    /**
     * Unsubscribes a user's channel from another channel.
     * @param subscriberUserProfile The user who is unsubscribing.
     * @param channelToUnsubscribeFromId The ID of the channel to unsubscribe from.
     * @throws ResourceNotFoundException if either channel or the subscription is not found.
     */
    @Transactional
    public void unsubscribeFromChannel(UserProfile subscriberUserProfile, Long channelToUnsubscribeFromId) {
        YoutubeChannel subscriberChannel = youtubeChannelRepository.findByUserProfile(subscriberUserProfile)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber channel not found for user: " + subscriberUserProfile.getEmail()));
        YoutubeChannel subscribedToChannel = getChannelById(channelToUnsubscribeFromId);

        ChannelSubscriber subscription = channelSubscriberRepository.findBySubscriberChannelAndSubscribedToChannel(subscriberChannel, subscribedToChannel)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found."));

        channelSubscriberRepository.delete(subscription);
    }

    /**
     * Checks if a user's channel is subscribed to another channel.
     * @param subscriberUserProfile The user who is checking.
     * @param targetChannelId The ID of the target channel.
     * @return True if subscribed, false otherwise.
     */
    public boolean isSubscribed(UserProfile subscriberUserProfile, Long targetChannelId) {
        Optional<YoutubeChannel> subscriberChannelOpt = youtubeChannelRepository.findByUserProfile(subscriberUserProfile);
        if (subscriberChannelOpt.isEmpty()) {
            return false; // Subscriber channel doesn't exist for this user
        }
        YoutubeChannel subscriberChannel = subscriberChannelOpt.get();
        YoutubeChannel targetChannel = getChannelById(targetChannelId); // Throws if not found

        return channelSubscriberRepository.findBySubscriberChannelAndSubscribedToChannel(subscriberChannel, targetChannel).isPresent();
    }

    /**
     * Gets the number of subscribers for a given channel.
     * @param channelId The ID of the channel.
     * @return The number of subscribers.
     * @throws ResourceNotFoundException if the channel is not found.
     */
    public long getSubscriberCount(Long channelId) {
        YoutubeChannel channel = getChannelById(channelId);
        return channelSubscriberRepository.countBySubscribedToChannel(channel);
    }
}
