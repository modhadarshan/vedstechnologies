package com.youtubeclone.controller;

import com.youtubeclone.dto.UserLoginRequest;
import com.youtubeclone.dto.UserRegisterRequest;
import com.youtubeclone.dto.UserResponse;
import com.youtubeclone.model.UserProfile;
import com.youtubeclone.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Registers a new user.
     * @param request The registration request containing email and password.
     * @return ResponseEntity with UserResponse on success.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        UserProfile newUser = userProfileService.registerUser(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(mapToUserResponse(newUser), HttpStatus.CREATED);
    }

    /**
     * Authenticates a user. This endpoint acts as a "login" mechanism by validating credentials
     * and returning user details. For subsequent requests, the user's email and password
     * are expected in headers.
     * @param request The login request containing email and password.
     * @return ResponseEntity with UserResponse on successful authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@Valid @RequestBody UserLoginRequest request) {
        UserProfile authenticatedUser = userProfileService.authenticateUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(mapToUserResponse(authenticatedUser));
    }

    /**
     * Helper method to map UserProfile to UserResponse DTO.
     * @param userProfile The UserProfile entity.
     * @return The UserResponse DTO.
     */
    private UserResponse mapToUserResponse(UserProfile userProfile) {
        UserResponse response = new UserResponse();
        response.setId(userProfile.getId());
        response.setEmail(userProfile.getEmail());
        response.setCreatedAt(userProfile.getCreatedAt());

        if (userProfile.getYoutubeChannel() != null) {
            UserResponse.ChannelResponse channelResponse = new UserResponse.ChannelResponse();
            channelResponse.setId(userProfile.getYoutubeChannel().getId());
            channelResponse.setName(userProfile.getYoutubeChannel().getName());
            channelResponse.setDescription(userProfile.getYoutubeChannel().getDescription());
            response.setYoutubeChannel(channelResponse);
        }
        return response;
    }
}
