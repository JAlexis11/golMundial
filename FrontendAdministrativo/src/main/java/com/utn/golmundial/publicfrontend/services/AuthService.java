package com.utn.golmundial.publicfrontend.services;

import com.utn.golmundial.publicfrontend.model.User;

public interface AuthService {

    User authenticate(String username, String password);

    boolean register(User newUser);

    boolean usernameExists(String username);
}