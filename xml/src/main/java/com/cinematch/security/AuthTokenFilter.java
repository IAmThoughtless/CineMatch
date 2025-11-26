package com.cinematch.security;

// Imports για το Spring Security, JWT και δικά μας Services/Utils
import com.cinematch.service.UserDetailsServiceImpl;
import com.cinematch.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Αυτό το φίλτρο ελέγχει το JWT Token σε κάθε εισερχόμενο αίτημα (request)
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils; // Χρειάζεται το JwtUtils για επικύρωση και ανάγνωση

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // Χρειάζεται για να φορτώσει τον χρήστη

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request); // 1. Λήψη Token

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) { // 2. Επικύρωση Token

                String username = jwtUtils.getUserNameFromJwtToken(jwt); // 3. Εξαγωγή Username

                // 4. Φόρτωση χρήστη και ρύθμιση της αυθεντικοποίησης στο Security Context
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            System.out.println("Cannot set user authentication: " + e);
        }

        // Συνέχιση στην αλυσίδα φίλτρων (επόμενος handler)
        filterChain.doFilter(request, response);
    }

    // Εξάγει το Token από την κεφαλίδα "Authorization: Bearer <token>"
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
