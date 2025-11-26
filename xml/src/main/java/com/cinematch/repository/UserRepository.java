package com.cinematch.repository;

import com.cinematch.model.User;
import java.util.Optional;

// Ορίζει τις βασικές λειτουργίες που χρειάζεται η εφαρμογή για να βρει/αποθηκεύσει χρήστες
public interface UserRepository {

    User save(User user);
    Optional<User> findByUsername(String username);
   Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

//package com.cinematch.repository;

//import java.util.Optional;

// Χρησιμοποιούμε το JpaRepository για να πάρουμε έτοιμες όλες τις CRUD λειτουργίες
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;

//import com.cinematch.model.User;

//@Repository
// Η κληρονομικότητα από το JpaRepository είναι αυτή που "μαγικά" υλοποιεί
// τις βασικές λειτουργίες (save, findById, κ.α.)
//public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Αυτή η μέθοδος υλοποιείται αυτόματα από το Spring Data JPA
    // με βάση το όνομά της ("findBy" + το όνομα του πεδίου "Username").
   // Optional<User> findByUsername(String username);

    // 2. Αυτή η μέθοδος υλοποιείται αυτόματα ("existsBy" + το όνομα του πεδίου "Username").
   // Boolean existsByUsername(String username);

    // 3. Χρειάζεται και για το email, όπως χρησιμοποιήθηκε στον AuthController.
   // Boolean existsByEmail(String email);

    // Σημείωση: Η μέθοδος save(User user) υπάρχει ήδη από το JpaRepository.
//}
