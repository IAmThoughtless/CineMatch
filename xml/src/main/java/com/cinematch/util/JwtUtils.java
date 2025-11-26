package com.cinematch.util;

// Imports για το JWT (io.jsonwebtoken)
import com.cinematch.model.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// Imports για το Spring
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// Το @Component το καθιστά διαθέσιμο για Autowire
@Component
public class JwtUtils {

    // Λαμβάνει το μυστικό κλειδί από το application.properties
    @Value("${cinematch.app.jwtSecret}")
    private String jwtSecret;

    // Λαμβάνει τη διάρκεια λήξης του token από το application.properties
    @Value("${cinematch.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Δημιουργεί ένα JWT Token με βάση την επιτυχημένη αυθεντικοποίηση.
     */
    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Το θέμα (Subject) είναι το username
                .setIssuedAt(new Date()) // Χρόνος έκδοσης
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Χρόνος λήξης
                .signWith(key(), SignatureAlgorithm.HS256) // Υπογραφή με το μυστικό κλειδί
                .compact(); // Τελική κατασκευή του string
    }

    /**
     * Επιστρέφει το κλειδί (Key) για την υπογραφή του JWT.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Επικυρώνει αν το JWT Token είναι έγκυρο.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    /**
     * Εξάγει το username από το JWT Token.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
