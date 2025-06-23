package com.youtubeclone.controller;

import com.youtubeclone.model.UserProfile;
import com.youtubeclone.service.AuthenticationService;
import com.youtubeclone.service.YoutubeChannelService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    @Autowired
    private YoutubeChannelService youtubeChannelService;

    @Autowired
    private AuthenticationService authService;

    /**
     * Subscribes the authenticated user's channel to another channel. Requires authentication.
     *
     * @param channelId The ID of the channel to subscribe to.
     * @param request HttpServletRequest to get authentication headers.
     * @return ResponseEntity with success status.
     */
    @PostMapping("/{channelId}/subscribe")
    public ResponseEntity<Void> subscribeToChannel(@PathVariable Long channelId, HttpServletRequest request) {
        UserProfile authenticatedUser = authService.getAuthenticatedUser(request);
        youtubeChannelService.subscribeToChannel(authenticatedUser, channelId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Unsubscribes the authenticated user's channel from another channel. Requires authentication.
     *
     * @param channelId The ID of the channel to unsubscribe from.
     * @param request HttpServletRequest to get authentication headers.
     * @return ResponseEntity with success status.
     */
    @DeleteMapping("/{channelId}/unsubscribe")
    public ResponseEntity<Void> unsubscribeFromChannel(@PathVariable Long channelId, HttpServletRequest request) {
        UserProfile authenticatedUser = authService.getAuthenticatedUser(request);
        youtubeChannelService.unsubscribeFromChannel(authenticatedUser, channelId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
    }

    /**
     * Checks if the authenticated user's channel is subscribed to another channel. Requires authentication.
     *
     * @param channelId The ID of the target channel.
     * @param request HttpServletRequest to get authentication headers.
     * @return ResponseEntity with boolean (true if subscribed, false otherwise).
     */
    @GetMapping("/{channelId}/is-subscribed")
    public ResponseEntity<Boolean> isSubscribed(@PathVariable Long channelId, HttpServletRequest request) {
        UserProfile authenticatedUser = authService.getAuthenticatedUser(request);
        boolean subscribed = youtubeChannelService.isSubscribed(authenticatedUser, channelId);
        return ResponseEntity.ok(subscribed);
    }

    /**
     * Gets the subscriber count for a channel. Can be accessed by guests.
     *
     * @param channelId The ID of the channel.
     * @return ResponseEntity with the subscriber count.
     */
    @GetMapping("/{channelId}/subscriber-count")
    public ResponseEntity<Long> getSubscriberCount(@PathVariable Long channelId) {
        long count = youtubeChannelService.getSubscriberCount(channelId);
        return ResponseEntity.ok(count);
    }
}
