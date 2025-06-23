
// service/VideoService.java
package com.youtubeclone.service;

import com.youtubeclone.exception.ResourceNotFoundException;
import com.youtubeclone.model.*;
import com.youtubeclone.repository.VideoLikeRepository;
import com.youtubeclone.repository.VideoRepository;
import com.youtubeclone.repository.VideoViewRepository;
import com.youtubeclone.repository.YoutubeChannelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private YoutubeChannelRepository youtubeChannelRepository;

    @Autowired
    private VideoViewRepository videoViewRepository;

    @Autowired
    private VideoLikeRepository videoLikeRepository;

    /**
     * Uploads a new video.
     *
     * @param title The video title.
     * @param description The video description.
     * @param category The video category.
     * @param videoFile The actual video file.
     * @param thumbnailFile An optional thumbnail image file.
     * @param uploaderUserProfile The user uploading the video.
     * @return The uploaded Video entity.
     * @throws IOException if there's an issue reading the file.
     * @throws ResourceNotFoundException if the uploader's channel is not found.
     */
    @Transactional
    public Video uploadVideo(String title, String description, String category, MultipartFile videoFile, MultipartFile thumbnailFile, UserProfile uploaderUserProfile) throws IOException {
        YoutubeChannel uploaderChannel = youtubeChannelRepository.findByUserProfile(uploaderUserProfile)
                .orElseThrow(() -> new ResourceNotFoundException("Uploader channel not found for user: " + uploaderUserProfile.getEmail()));

        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setCategory(category);
        video.setVideoData(videoFile.getBytes()); // Store video data as BLOB
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            video.setThumbnailData(thumbnailFile.getBytes()); // Store thumbnail data as BLOB
        }
        video.setYoutubeChannel(uploaderChannel);
        video.setViewCount(0L); // Initialize view count

        return videoRepository.save(video);
    }

    /**
     * Retrieves a video by its ID.
     *
     * @param videoId The ID of the video.
     * @return The Video entity.
     * @throws ResourceNotFoundException if the video is not found.
     */
    public Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with ID: " + videoId));
    }

    /**
     * Retrieves a paginated list of all videos.
     * @param page The page number (0-indexed).
     * @param size The number of videos per page.
     * @return A Page of Video entities.
     */
    public Page<Video> getAllVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return videoRepository.findAll(pageable);
    }

    /**
     * Increments the view count for a video.
     *
     * @param videoId The ID of the video.
     * @param userProfile The user who viewed the video (can be null for guests).
     * @throws ResourceNotFoundException if the video is not found.
     */
    @Transactional
    public void incrementViewCount(Long videoId, UserProfile userProfile) {
        Video video = getVideoById(videoId);

        // Record a new view entry
        VideoView videoView = new VideoView();
        videoView.setVideo(video);
        videoView.setUserProfile(userProfile); // userProfile can be null for guests
        videoViewRepository.save(videoView);

        // Increment the view count on the video itself (optimistic locking not implemented for simplicity)
        video.setViewCount(video.getViewCount() + 1);
        videoRepository.save(video);
    }

    /**
     * Likes a video for a given user. If already liked, it unlikes it.
     *
     * @param videoId The ID of the video.
     * @param userProfile The user liking/unliking the video.
     * @return True if liked, False if unliked.
     * @throws ResourceNotFoundException if the video is not found.
     */
    @Transactional
    public boolean toggleVideoLike(Long videoId, UserProfile userProfile) {
        Video video = getVideoById(videoId);
        Optional<VideoLike> existingLike = videoLikeRepository.findByVideoAndUserProfile(video, userProfile);

        if (existingLike.isPresent()) {
            videoLikeRepository.delete(existingLike.get());
            return false; // Unliked
        } else {
            VideoLike newLike = new VideoLike();
            newLike.setVideo(video);
            newLike.setUserProfile(userProfile);
            videoLikeRepository.save(newLike);
            return true; // Liked
        }
    }

    /**
     * Checks if a user has liked a specific video.
     * @param videoId The ID of the video.
     * @param userProfile The user to check.
     * @return True if the user has liked the video, false otherwise.
     * @throws ResourceNotFoundException if the video is not found.
     */
    public boolean hasUserLikedVideo(Long videoId, UserProfile userProfile) {
        Video video = getVideoById(videoId);
        return videoLikeRepository.findByVideoAndUserProfile(video, userProfile).isPresent();
    }

    /**
     * Gets the number of likes for a given video.
     * @param videoId The ID of the video.
     * @return The number of likes.
     * @throws ResourceNotFoundException if the video is not found.
     */
    public long getVideoLikeCount(Long videoId) {
        Video video = getVideoById(videoId);
        return videoLikeRepository.countByVideo(video);
    }
}
