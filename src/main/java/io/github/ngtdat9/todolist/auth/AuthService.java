package io.github.ngtdat9.todolist.auth;

import io.github.ngtdat9.todolist.model.User;
import io.github.ngtdat9.todolist.storage.Database;
import io.github.ngtdat9.todolist.storage.UserRepository;

import java.util.Optional;

public class AuthService {
    private static final AuthService instance = new AuthService(new UserRepository(Database.getInstance()));

    // Unknown usernames are checked against this, so they take as long to reject as a wrong password.
    private static final String DECOY_HASH = PasswordHasher.hash("decoy");

    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
    }

    public static AuthService getInstance() {
        return instance;
    }

    /** Returns the new user, or empty if the username is already taken. */
    public Optional<User> signUp(String username, String password) {
        return users.create(username.trim(), PasswordHasher.hash(password));
    }

    /** Returns the user, or empty if the username or the password is wrong. */
    public Optional<User> logIn(String username, String password) {
        Optional<UserRepository.StoredCredentials> credentials = users.findCredentials(username.trim());
        String storedHash = credentials.map(UserRepository.StoredCredentials::passwordHash).orElse(DECOY_HASH);
        boolean passwordMatches = PasswordHasher.verify(password, storedHash);
        return credentials.filter(found -> passwordMatches).map(UserRepository.StoredCredentials::user);
    }
}
