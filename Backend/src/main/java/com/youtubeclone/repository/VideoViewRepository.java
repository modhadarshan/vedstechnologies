package com.youtubeclone.repository;

import com.youtubeclone.model.VideoView;
import com.youtubeclone.model.Video;
import com.youtubeclone.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoViewRepository extends JpaRepository<VideoView, Long> {
    Optional<VideoView> findByVideoAndUserProfile(Video video, UserProfile userProfile);
    long countByVideo(Video video);
}