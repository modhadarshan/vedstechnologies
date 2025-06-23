package com.youtubeclone.repository;

import com.youtubeclone.model.CommentLike;
import com.youtubeclone.model.VideoComment;
import com.youtubeclone.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByVideoCommentAndUserProfile(VideoComment videoComment, UserProfile userProfile);
    long countByVideoComment(VideoComment videoComment);
}