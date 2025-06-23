package com.youtubeclone.controller;

import com.youtubeclone.dto.CommentRequest;
import com.youtubeclone.dto.CommentResponse;
import com.youtubeclone.model.UserProfile;
import com.youtubeclone.model.VideoComment;
import com.youtubeclone.service.AuthenticationService;
import com.youtubeclone.service.VideoCommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private VideoCommentService videoCommentService;

    @Autowired
    private AuthenticationService authService;

    /**
     * Adds a new comment to a video or replies to an existing comment. Requires authentication.
     *
     * @param videoId The ID of the video (if new comment) or the video of the parent comment.
     * @param request HttpServletRequest to get authentication headers.
     * @param commentRequest The comment request containing content and optional parentCommentId.
     * @return ResponseEntity with CommentResponse on success.
     */
    @PostMapping("/video/{videoId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long videoId,
            HttpServletRequest request,
            @Valid @RequestBody CommentRequest commentRequest) {

        UserProfile authenticatedUser = authService.getAuthenticatedUser(request);

        VideoComment newComment;
        if (commentRequest.getParentCommentId() != null) {
            newComment = videoCommentService.addReplyToComment(commentRequest.getParentCommentId(), commentRequest.getContent(), authenticatedUser);
        } else {
            newComment = videoCommentService.addComment(videoId, commentRequest.getContent(), authenticatedUser);
        }
        // Need to pass authenticatedUser to mapToCommentResponse to determine userLiked status
        return new ResponseEntity<>(mapToCommentResponse(newComment, authenticatedUser), HttpStatus.CREATED);
    }

    /**
     * Gets all top-level comments for a specific video. Can be accessed by guests.
     *
     * @param videoId The ID of the video.
     * @param request HttpServletRequest to optionally get user for liked status.
     * @return ResponseEntity with a list of CommentResponse.
     */
    @GetMapping("/video/{videoId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForVideo(
            @PathVariable Long videoId,
            HttpServletRequest request) {
        UserProfile userProfile = authService.getAuthenticatedUserOptional(request).orElse(null); // Guests can view

        List<VideoComment> comments = videoCommentService.getCommentsForVideo(videoId);
        List<CommentResponse> responses = comments.stream()
                .map(comment -> mapToCommentResponse(comment, userProfile))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Gets all replies for a specific parent comment. Can be accessed by guests.
     *
     * @param parentCommentId The ID of the parent comment.
     * @param request HttpServletRequest to optionally get user for liked status.
     * @return ResponseEntity with a list of CommentResponse.
     */
    @GetMapping("/{parentCommentId}/replies")
    public ResponseEntity<List<CommentResponse>> getRepliesForComment(
            @PathVariable Long parentCommentId,
            HttpServletRequest request) {
        UserProfile userProfile = authService.getAuthenticatedUserOptional(request).orElse(null); // Guests can view

        List<VideoComment> replies = videoCommentService.getRepliesForComment(parentCommentId);
        List<CommentResponse> responses = replies.stream()
                .map(reply -> mapToCommentResponse(reply, userProfile))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Toggles a comment like/unlike. Requires authentication.
     *
     * @param commentId The ID of the comment.
     * @param request HttpServletRequest to get authentication headers.
     * @return ResponseEntity with a boolean indicating if the comment is now liked (true) or unliked (false).
     */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Boolean> toggleCommentLike(@PathVariable Long commentId, HttpServletRequest request) {
        UserProfile authenticatedUser = authService.getAuthenticatedUser(request);
        boolean isLiked = videoCommentService.toggleCommentLike(commentId, authenticatedUser);
        return ResponseEntity.ok(isLiked);
    }

    /**
     * Gets the like count for a comment. Can be accessed by guests.
     *
     * @param commentId The ID of the comment.
     * @return ResponseEntity with the like count.
     */
    @GetMapping("/{commentId}/like-count")
    public ResponseEntity<Long> getCommentLikeCount(@PathVariable Long commentId) {
        long likeCount = videoCommentService.getCommentLikeCount(commentId);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * Checks if the authenticated user has liked a comment. Requires authentication.
     *
     * @param commentId The ID of the comment.
     * @param request HttpServletRequest to get authentication headers.
     * @return ResponseEntity with boolean (true if liked, false otherwise).
     */
    @GetMapping("/{commentId}/user-liked")
    public ResponseEntity<Boolean> hasUserLikedComment(@PathVariable Long commentId, HttpServletRequest request) {
        UserProfile authenticatedUser = authService.getAuthenticatedUser(request);
        boolean liked = videoCommentService.hasUserLikedComment(commentId, authenticatedUser);
        return ResponseEntity.ok(liked);
    }


    /**
     * Helper method to map VideoComment to CommentResponse DTO.
     * Includes logic to check if a user liked the comment, if userProfile is provided.
     *
     * @param comment The VideoComment entity.
     * @param currentUserProfile The authenticated UserProfile (can be null for guests).
     * @return The CommentResponse DTO.
     */
    private CommentResponse mapToCommentResponse(VideoComment comment, UserProfile currentUserProfile) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);

        // Map user info
        CommentResponse.UserInfo userInfo = new CommentResponse.UserInfo();
        userInfo.setId(comment.getUserProfile().getId());
        userInfo.setEmail(comment.getUserProfile().getEmail());

        // Map channel info within user info
        if (comment.getUserProfile().getYoutubeChannel() != null) {
            CommentResponse.UserInfo.ChannelInfo channelInfo = new CommentResponse.UserInfo.ChannelInfo();
            channelInfo.setId(comment.getUserProfile().getYoutubeChannel().getId());
            channelInfo.setName(comment.getUserProfile().getYoutubeChannel().getName());
            userInfo.setYoutubeChannel(channelInfo);
        }
        response.setUserProfile(userInfo);

        // Get like count
        response.setLikeCount(videoCommentService.getCommentLikeCount(comment.getId()));

        // Check if current user liked this comment
        if (currentUserProfile != null) {
            response.setUserLiked(videoCommentService.hasUserLikedComment(comment.getId(), currentUserProfile));
        } else {
            response.setUserLiked(false); // Guests cannot like, so they haven't liked it
        }

        return response;
    }
}
