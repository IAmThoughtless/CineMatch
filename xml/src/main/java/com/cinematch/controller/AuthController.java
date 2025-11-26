package com.cinematch.controller;

// Εισαγωγές του Spring
import com.cinematch.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired; // Για Autowired components
import org.springframework.http.ResponseEntity; // Για να επιστρέφει HTTP Responses
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Τύπος Token για Login
import org.springframework.security.core.Authentication; // Το αντικείμενο πιστοποίησης
import org.springframework.security.core.context.SecurityContextHolder; // Για να βάζει τον χρήστη στο session
import org.springframework.web.bind.annotation.PostMapping; // Για τα POST requests
import org.springframework.web.bind.annotation.RequestBody; // Για να διαβάζει το JSON Body
import org.springframework.web.bind.annotation.RequestMapping; // Για το βασικό path (/api/auth)
import org.springframework.web.bind.annotation.RestController; // Για να δηλωθεί ως Controller

// Εισαγωγές τρίτων βιβλιοθηκών
import jakarta.validation.Valid; // Για να επικυρώνει τα input data (π.χ. @Email)

// Εισαγωγές των δικών μας κλάσεων
import com.cinematch.model.User;// Το μοντέλο χρήστη
import com.cinematch.payload.request.LoginRequest; // Το DTO για το Login
import com.cinematch.payload.request.RegisterRequest; // Το DTO για το Register
import com.cinematch.payload.response.JwtResponse; // Το DTO για την απάντηση Login
import com.cinematch.util.JwtUtils; // Νέο import
import com.cinematch.repository.UserRepository;
import com.cinematch.model.UserDetailsImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // 1. Απαραίτητο για το Login. Αυτό δημιουργεί το σφάλμα σου!
    @Autowired
    AuthenticationManager authenticationManager;

    // 2. Απαραίτητο για την αποθήκευση του χρήστη (Register)
    @Autowired
    UserRepository userRepository;

    // 3. Απαραίτητο για την κρυπτογράφηση του κωδικού (Register)
    @Autowired
    PasswordEncoder encoder;

    // 4. Απαραίτητο για τη δημιουργία του JWT Token (Login)
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. Αυθεντικοποίηση
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Δημιουργία ΠΡΑΓΜΑΤΙΚΟΥ JWT Token
        String jwt = jwtUtils.generateJwtToken(authentication); // <<< Η ΑΛΛΑΓΗ ΕΙΝΑΙ ΕΔΩ!

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // 3. Επιστροφή απάντησης με το Token
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail()));
    }



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {

        // 1. Έλεγχος αν υπάρχει ήδη το username στη βάση

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Σφάλμα: Το Username χρησιμοποιείται ήδη!"));
        }

        // 2. Έλεγχος αν υπάρχει ήδη το email στη βάση
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Σφάλμα: Το Email χρησιμοποιείται ήδη!"));
        }

        // 3. Δημιουργία νέου User object
        User user = new User(registerRequest.getUsername(),
                registerRequest.getEmail(),
                // Κρυπτογράφηση κωδικού πριν την αποθήκευση
                encoder.encode(registerRequest.getPassword()));

        // 4. Αποθήκευση χρήστη
        userRepository.save(user);

        // 5. Επιστροφή επιτυχίας
        return ResponseEntity.ok(new MessageResponse("Ο χρήστης καταχωρήθηκε επιτυχώς!"));
    }
}

    // ...

