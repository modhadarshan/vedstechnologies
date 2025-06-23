package com.youtubeclone.repository;

import com.youtubeclone.model.YoutubeChannel;
import com.youtubeclone.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YoutubeChannelRepository extends JpaRepository<YoutubeChannel, Long> {
    Optional<YoutubeChannel> findByUserProfile(UserProfile userProfile);
    Optional<YoutubeChannel> findByName(String name);
}