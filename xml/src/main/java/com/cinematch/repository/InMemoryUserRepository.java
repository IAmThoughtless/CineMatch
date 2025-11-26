package com.cinematch.repository;

import com.cinematch.model.User;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository // Λέει στο Spring ότι αυτή η κλάση είναι ένα Repository και πρέπει να το διαχειριστεί
public class InMemoryUserRepository implements UserRepository{

    // HashMap: Χρησιμοποιούμε το username ως κλειδί, καθώς είναι μοναδικό.
    // Αυτό είναι η "προσωρινή βάση δεδομένων"
    private final Map<String, User> userStorage = new HashMap<>();

    // Μετρητής για να δίνουμε μοναδικό ID σε κάθε νέο χρήστη
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public User save(User user) {
        // Αν ο χρήστης δεν έχει ID (είναι νέος), του δίνουμε ένα
        if (user.getId() == null) {
            user.setId(idCounter.incrementAndGet());
        }
        // Αποθηκεύουμε τον χρήστη στο Map με κλειδί το username
        userStorage.put(user.getUsername(), user);
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        // Επιστρέφουμε τον χρήστη, αν υπάρχει
        return Optional.ofNullable(userStorage.get(username));
    }

    @Override
    public Boolean existsByUsername(String username) {
        // Ελέγχουμε αν υπάρχει ήδη αυτό το username στο Map
        return userStorage.containsKey(username);
    }
}
