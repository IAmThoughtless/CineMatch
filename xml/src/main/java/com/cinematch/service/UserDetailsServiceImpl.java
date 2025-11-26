package com.cinematch.service;

import com.cinematch.model.UserDetailsImpl;
import com.cinematch.model.User;
import com.cinematch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

// Το @Service δηλώνει ότι αυτή η κλάση περιέχει επιχειρησιακή λογική
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Το Spring θα βρει το InMemoryUserRepository και θα το κάνει inject εδώ
    @Autowired
    UserRepository userRepository;

    // Αυτή η μέθοδος είναι υποχρεωτική από το Spring Security
    @Override
    @Transactional // Χρησιμοποιείται για να εξασφαλίσει ότι η λειτουργία ολοκληρώνεται
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Ψάχνει τον χρήστη στο Repository (δηλαδή στο HashMap μας)
        User user = userRepository.findByUsername(username)
                // 2. Αν δεν βρεθεί ο χρήστης, πετάει την απαραίτητη εξαίρεση του Spring Security
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // 3. Μετατρέπει το δικό μας User object σε UserDetails object που καταλαβαίνει το Spring Security
        return UserDetailsImpl.build(user);
    }
}
