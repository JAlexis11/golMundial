package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class AuthServiceMock implements AuthService {

    private final List<User> users = new ArrayList<>();
    private final AtomicLong sequenceId = new AtomicLong(1);

    public AuthServiceMock() {
        User testUser = new User(sequenceId.getAndIncrement(),
                "fan1", "fan1@example.com", "Test Fan", "USER");
        testUser.setPassword("pass123");
        users.add(testUser);
    }

    @Override
    public User authenticate(String username, String password) {
        // TODO: replace with a REST call to the centralized login endpoint
        // once Alexis's backend exposes it.
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public boolean register(User newUser) {
        if (usernameExists(newUser.getUsername())) {
            return false;
        }
        newUser.setId(sequenceId.getAndIncrement());
        newUser.setRole("USER");
        users.add(newUser);
        // TODO: the real backend grants a 10 UTNGolCoin welcome bonus here (RF01).
        return true;
    }

    @Override
    public boolean usernameExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }
}