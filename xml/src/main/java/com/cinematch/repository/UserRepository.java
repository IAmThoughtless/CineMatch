package com.cinematch.repository;

import com.cinematch.model.User;
import java.util.Optional;

// Ορίζει τις βασικές λειτουργίες που χρειάζεται η εφαρμογή για να βρει/αποθηκεύσει χρήστες
public interface UserRepository {

    User save(User user);
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
}
