package com.youtubeclone.service;

import com.youtubeclone.exception.ResourceNotFoundException;
import com.youtubeclone.model.CommentLike;
import com.youtubeclone.model.UserProfile;
import com.youtubeclone.model.Video;
import com.youtubeclone.model.VideoComment;
import com.youtubeclone.repository.CommentLikeRepository;
import com.youtubeclone.repository.VideoCommentRepository;
import com.youtubeclone.repository.VideoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideoCommentService {

    @Autowired
    private VideoCommentRepository videoCommentRepository;

    @Autowired
    private VideoRepository videoRepository; // To retrieve video by ID

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    /**
     * Adds a new comment to a video.
     *
     * @param videoId The ID of the video the comment belongs to.
     * @param content The content of the comment.
     * @param userProfile The user who is commenting.
     * @return The created VideoComment entity.
     * @throws ResourceNotFoundException if the video is not found.
     */
    @Transactional
    public VideoComment addComment(Long videoId, String content, UserProfile userProfile) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with ID: " + videoId));

        VideoComment comment = new VideoComment();
        comment.setContent(content);
        comment.setVideo(video);
        comment.setUserProfile(userProfile);
        return videoCommentRepository.save(comment);
    }

    /**
     * Adds a reply to an existing comment.
     *
     * @param parentCommentId The ID of the parent comment.
     * @param content The content of the reply.
     * @param userProfile The user who is replying.
     * @return The created VideoComment (reply) entity.
     * @throws ResourceNotFoundException if the parent comment is not found.
     */
    @Transactional
    public VideoComment addReplyToComment(Long parentCommentId, String content, UserProfile userProfile) {
        VideoComment parentComment = videoCommentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with ID: " + parentCommentId));

        VideoComment reply = new VideoComment();
        reply.setContent(content);
        reply.setVideo(parentComment.getVideo()); // Inherit video from parent comment
        reply.setUserProfile(userProfile);
        reply.setParentComment(parentComment);
        return videoCommentRepository.save(reply);
    }

    /**
     * Gets all top-level comments for a video, ordered by creation date (descending).
     * @param videoId The ID of the video.
     * @return A list of top-level VideoComment entities.
     * @throws ResourceNotFoundException if the video is not found.
     */
    public List<VideoComment> getCommentsForVideo(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with ID: " + videoId));
        return videoCommentRepository.findByVideoAndParentCommentIsNullOrderByCreatedAtDesc(video);
    }

    /**
     * Gets all replies for a specific parent comment, ordered by creation date (ascending).
     * @param parentCommentId The ID of the parent comment.
     * @return A list of VideoComment entities (replies).
     * @throws ResourceNotFoundException if the parent comment is not found.
     */
    public List<VideoComment> getRepliesForComment(Long parentCommentId) {
        VideoComment parentComment = videoCommentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with ID: " + parentCommentId));
        return videoCommentRepository.findByParentCommentOrderByCreatedAtAsc(parentComment);
    }

    /**
     * Likes a comment for a given user. If already liked, it unlikes it.
     *
     * @param commentId The ID of the comment.
     * @param userProfile The user liking/unliking the comment.
     * @return True if liked, False if unliked.
     * @throws ResourceNotFoundException if the comment is not found.
     */
    @Transactional
    public boolean toggleCommentLike(Long commentId, UserProfile userProfile) {
        VideoComment comment = videoCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));
        Optional<CommentLike> existingLike = commentLikeRepository.findByVideoCommentAndUserProfile(comment, userProfile);

        if (existingLike.isPresent()) {
            commentLikeRepository.delete(existingLike.get());
            return false; // Unliked
        } else {
            CommentLike newLike = new CommentLike();
            newLike.setVideoComment(comment);
            newLike.setUserProfile(userProfile);
            commentLikeRepository.save(newLike);
            return true; // Liked
        }
    }

    /**
     * Checks if a user has liked a specific comment.
     * @param commentId The ID of the comment.
     * @param userProfile The user to check.
     * @return True if the user has liked the comment, false otherwise.
     * @throws ResourceNotFoundException if the comment is not found.
     */
    public boolean hasUserLikedComment(Long commentId, UserProfile userProfile) {
        VideoComment comment = videoCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));
        return commentLikeRepository.findByVideoCommentAndUserProfile(comment, userProfile).isPresent();
    }

    /**
     * Gets the number of likes for a given comment.
     * @param commentId The ID of the comment.
     * @return The number of likes.
     * @throws ResourceNotFoundException if the comment is not found.
     */
    public long getCommentLikeCount(Long commentId) {
        VideoComment comment = videoCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));
        return commentLikeRepository.countByVideoComment(comment);
    }
}