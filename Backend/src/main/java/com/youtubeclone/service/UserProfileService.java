package com.youtubeclone.service;

import com.youtubeclone.exception.AuthenticationException;
import com.youtubeclone.exception.DuplicateResourceException;
import com.youtubeclone.exception.ResourceNotFoundException;
import com.youtubeclone.model.UserProfile;
import com.youtubeclone.model.YoutubeChannel;
import com.youtubeclone.repository.UserProfileRepository;
import com.youtubeclone.repository.YoutubeChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private YoutubeChannelRepository youtubeChannelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user and automatically creates a YouTube channel for them.
     *
     * @param email The user's email.
     * @param password The user's plain text password.
     * @return The created UserProfile.
     * @throws DuplicateResourceException if a user with the given email already exists.
     */
    @Transactional
    public UserProfile registerUser(String email, String password) {
        if (userProfileRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User with email " + email + " already exists.");
        }

        UserProfile newUser = new UserProfile();
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(password)); // Hash the password

        // Save the user first to get an ID for channel creation
        newUser = userProfileRepository.save(newUser);

        // Auto-create YouTube channel for the new user
        YoutubeChannel newChannel = new YoutubeChannel();
        newChannel.setName(generateUniqueChannelName(email)); // Generate a unique channel name
        newChannel.setDescription("Default channel for " + email);
        newChannel.setUserProfile(newUser); // Link channel to user profile

        youtubeChannelRepository.save(newChannel);

        // Link channel back to user profile for bi-directional relationship management
        newUser.setYoutubeChannel(newChannel);
        return userProfileRepository.save(newUser);
    }

    /**
     * Authenticates a user based on email and password.
     *
     * @param email The user's email.
     * @param password The user's plain text password.
     * @return The authenticated UserProfile.
     * @throws AuthenticationException if authentication fails.
     */
    public UserProfile authenticateUser(String email, String password) {
        UserProfile userProfile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

        if (!passwordEncoder.matches(password, userProfile.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password.");
        }
        return userProfile;
    }

    /**
     * Finds a user profile by ID.
     * @param userId The ID of the user.
     * @return The UserProfile.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public UserProfile getUserProfileById(Long userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    /**
     * Finds a user profile by email.
     * @param email The email of the user.
     * @return The UserProfile.
     * @throws ResourceNotFoundException if the user is not found.
     */
    public UserProfile getUserProfileByEmail(String email) {
        return userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }


    /**
     * Generates a unique channel name based on the user's email.
     * In a real application, this might involve more sophisticated logic
     * or allow users to choose their own channel names.
     * @param email The user's email.
     * @return A unique channel name.
     */
    private String generateUniqueChannelName(String email) {
        String baseName = email.split("@")[0]; // Use part before '@'
        String channelName = baseName + "-channel";
        int counter = 1;
        while (youtubeChannelRepository.findByName(channelName).isPresent()) {
            channelName = baseName + "-channel-" + counter++;
        }
        return channelName;
    }
}