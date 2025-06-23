package com.youtubeclone.repository;

import com.youtubeclone.model.VideoComment;
import com.youtubeclone.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoCommentRepository extends JpaRepository<VideoComment, Long> {
    List<VideoComment> findByVideoAndParentCommentIsNullOrderByCreatedAtDesc(Video video); // Top-level comments
    List<VideoComment> findByParentCommentOrderByCreatedAtAsc(VideoComment parentComment); // Replies
    long countByVideo(Video video);
}
