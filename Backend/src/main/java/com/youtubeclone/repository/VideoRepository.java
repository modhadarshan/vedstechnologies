package com.youtubeclone.repository;

import com.youtubeclone.model.Video;
import com.youtubeclone.model.YoutubeChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findAll(Pageable pageable);
    Page<Video> findByYoutubeChannel(YoutubeChannel channel, Pageable pageable);
}