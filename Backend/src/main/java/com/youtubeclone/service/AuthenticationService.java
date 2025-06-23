package com.youtubeclone.service;

import com.youtubeclone.exception.AuthenticationException;
import com.youtubeclone.model.UserProfile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Authenticates a user based on email and password provided in custom headers.
     *
     * @param request The HttpServletRequest to extract headers from.
     * @return The authenticated UserProfile.
     * @throws AuthenticationException if authentication fails or headers are missing.
     */
    public UserProfile getAuthenticatedUser(HttpServletRequest request) {
        String email = request.getHeader("X-User-Email");
        String password = request.getHeader("X-User-Password");

        if (email == null || password == null) {
            throw new AuthenticationException("Authentication headers (X-User-Email, X-User-Password) are missing.");
        }

        return userProfileService.authenticateUser(email, password);
    }

    /**
     * Attempts to get an authenticated user, returning an Optional.
     * Useful for actions that are allowed for both authenticated and guest users.
     *
     * @param request The HttpServletRequest to extract headers from.
     * @return An Optional containing the authenticated UserProfile if successful, empty otherwise.
     */
    public Optional<UserProfile> getAuthenticatedUserOptional(HttpServletRequest request) {
        try {
            return Optional.of(getAuthenticatedUser(request));
        } catch (AuthenticationException e) {
            return Optional.empty(); // Not authenticated
        }
    }
}