package com.vityarthi.booking.service;

import com.vityarthi.booking.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user accounts and authentication lookups.
 */
public class UserService {
    private final Map<String, User> userRegistry = new ConcurrentHashMap<>();

    public void registerUser(User user) {
        userRegistry.put(user.getUserId().toLowerCase(), user);
    }

    public Optional<User> getUserById(String userId) {
        if (userId == null) return Optional.empty();
        return Optional.ofNullable(userRegistry.get(userId.toLowerCase()));
    }

    public Collection<User> getAllUsers() {
        return userRegistry.values();
    }
}
