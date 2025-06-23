package com.youtubeclone.controller;

import com.youtubeclone.dto.VideoResponse;
import com.youtubeclone.dto.VideoUploadRequest;
import com.youtubeclone.model.UserProfile;
import com.youtubeclone.model.Video;
import com.youtubeclone.service.AuthenticationService;
import com.youtubeclone.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private AuthenticationService authService; // To get authenticated user from headers

    /**
     * Uploads a new video. Requires authentication.
     *
     * @param request HttpServletRequest to get authentication headers.
     * @param videoFile The video file.
     * @param thumbnailFile An optional thumbnail file.
     * @param uploadRequest Video metadata (title, description, category).
     * @return ResponseEntity with VideoResponse on success.
     * @throws IOException if file handling fails.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoResponse> uploadVideo(
            HttpServletRequest request,
            @RequestPart("videoFile") MultipartFile videoFile,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @Valid @RequestPart("uploadRequest") VideoUploadRequest uploadRequest) throws IOException {

        UserProfile authenticatedUser = authService.getAuthenticatedUser(request); // Throws AuthenticationException if not authenticated

        Video uploadedVideo = videoService.uploadVideo(
                uploadRequest.getTitle(),
                uploadRequest.getDescription(),
                uploadRequest.getCategory(),
                videoFile,
                thumbnailFile,
                authenticatedUser
        );
        return new ResponseEntity<>(mapToVideoResponse(uploadedVideo, authenticatedUser), HttpStatus.CREATED);
    }

    /**
     * Get a specific video by ID. Can be accessed by guests.
     * Also increments view count.
     *
     * @param id The ID of the video.
     * @param request HttpServletRequest to optionally get user for view tracking.
     * @return ResponseEntity with VideoResponse.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VideoResponse> getVideoById(@PathVariable Long id, HttpServletRequest request) {
        UserProfile userProfile = authService.getAuthenticatedUserOptional(request).orElse(null); // Guests can view

        videoService.incrementViewCount(id, userProfile); // Increment view count
        Video video = videoService.getVideoById(id);
        return ResponseEntity.ok(mapToVideoResponse(video, userProfile));
    }

    /**
     * Streams video data.
     *
     * @param id The ID of the video.
     * @return ResponseEntity with video data as byte array.
     */
    @GetMapping("/{id}/stream")
    public ResponseEntity<byte[]> streamVideo(@PathVariable Long id) {
        Video video = videoService.getVideoById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4")); // Assuming MP4 for simplicity
        headers.setContentLength(video.getVideoData().length);
        return new ResponseEntity<>(video.getVideoData(), headers, HttpStatus.OK);
    }

    /**
     * Gets a paginated list of all videos. Can be accessed by guests.
     *
     * @param page The page number (0-indexed).
     * @param size The number of videos per page.
     * @param request HttpServletRequest to optionally get user for liked status.
     * @return Page of VideoResponse.
     */
    @GetMapping
    public ResponseEntity<Page<VideoResponse>> getAllVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        UserProfile userProfile = authService.getAuthenticatedUserOptional(request).orElse(null); // Guests can view

        Page<Video> videosPage = videoService.getAllVideos(page, size);
        Page<VideoResponse> responsePage = videosPage.map(video -> mapToVideoResponse(video, userProfile));
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Toggles a video like/unlike. Requires authentication.
     *
     * @param id The ID of the video.
     * @param request HttpServletRequest to get authentication headers.
     * @return ResponseEntity with a boolean indicating if the video is now liked (true) or unliked (false).
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Boolean> toggleVideoLike(@PathVariable Long id, HttpServletRequest request) {
        UserProfile authenticatedUser = authService.getAuthenticatedUser(request);
        boolean isLiked = videoService.toggleVideoLike(id, authenticatedUser);
        return ResponseEntity.ok(isLiked);
    }

    /**
     * Gets the like count for a video. Can be accessed by guests.
     *
     * @param id The ID of the video.
     * @return ResponseEntity with the like count.
     */
    @GetMapping("/{id}/like-count")
    public ResponseEntity<Long> getVideoLikeCount(@PathVariable Long id) {
        long likeCount = videoService.getVideoLikeCount(id);
        return ResponseEntity.ok(likeCount);
    }

    /**
     * Checks if the authenticated user has liked a video. Requires authentication.
     *
     * @param id The ID of the video.
     * @param request HttpServletRequest to get authentication headers.
     * @return ResponseEntity with boolean (true if liked, false otherwise).
     */
    @GetMapping("/{id}/user-liked")
    public ResponseEntity<Boolean> hasUserLikedVideo(@PathVariable Long id, HttpServletRequest request) {
        UserProfile authenticatedUser = authService.getAuthenticatedUser(request);
        boolean liked = videoService.hasUserLikedVideo(id, authenticatedUser);
        return ResponseEntity.ok(liked);
    }

    /**
     * Helper method to map Video to VideoResponse DTO.
     * Includes logic to check if a user liked the video, if userProfile is provided.
     * @param video The Video entity.
     * @param userProfile The authenticated UserProfile (can be null for guests).
     * @return The VideoResponse DTO.
     */
    private VideoResponse mapToVideoResponse(Video video, UserProfile userProfile) {
        VideoResponse response = new VideoResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setDescription(video.getDescription());
        response.setCategory(video.getCategory());
        response.setCreatedAt(video.getCreatedAt());
        response.setViewCount(video.getViewCount());
        response.setThumbnailData(video.getThumbnailData()); // Include thumbnail

        if (video.getYoutubeChannel() != null) {
            VideoResponse.ChannelInfo channelInfo = new VideoResponse.ChannelInfo();
            channelInfo.setId(video.getYoutubeChannel().getId());
            channelInfo.setName(video.getYoutubeChannel().getName());
            response.setYoutubeChannel(channelInfo);
        }
        return response;
    }
}
