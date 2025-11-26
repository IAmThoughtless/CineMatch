//package com.cinematch.repository;

//import com.cinematch.model.User;
//import org.springframework.stereotype.Repository;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicLong;

//@Repository // Λέει στο Spring ότι αυτή η κλάση είναι ένα Repository και πρέπει να το διαχειριστεί
//public class InMemoryUserRepository implements UserRepository{

    // HashMap: Χρησιμοποιούμε το username ως κλειδί, καθώς είναι μοναδικό.
    // Αυτό είναι η "προσωρινή βάση δεδομένων"
    //private final Map<String, User> userStorage = new HashMap<>();

    // Μετρητής για να δίνουμε μοναδικό ID σε κάθε νέο χρήστη
    //private final AtomicLong idCounter = new AtomicLong(0);

    //@Override
    //public User save(User user) {
        // Αν ο χρήστης δεν έχει ID (είναι νέος), του δίνουμε ένα
        //if (user.getId() == null) {
          //  user.setId(idCounter.incrementAndGet());
        //}
        // Αποθηκεύουμε τον χρήστη στο Map με κλειδί το username
        //userStorage.put(user.getUsername(), user);
      //  return user;
    //}

    //@Override
    //public Optional<User> findByUsername(String username) {
        // Επιστρέφουμε τον χρήστη, αν υπάρχει
      //  return Optional.ofNullable(userStorage.get(username));
    //}

    //@Override
   // public Boolean existsByUsername(String username) {
        // Ελέγχουμε αν υπάρχει ήδη αυτό το username στο Map
    //    return userStorage.containsKey(username);
  //  }
//}

package com.cinematch.repository;

import com.cinematch.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Υλοποίηση του UserRepository που αποθηκεύει δεδομένα στη μνήμη (για testing).
 * ΣΗΜΑΝΤΙΚΟ: Αυτό δεν είναι για παραγωγή.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {

    // Χρήση HashMap για αποθήκευση, όπου key=ID και value=User
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    // Χρήση AtomicLong για να προσομοιώσει την αυτόματη αύξηση ID της βάσης
    private final AtomicLong currentId = new AtomicLong(1);

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            // Αν είναι νέος χρήστης, δώσε του νέο ID
            user.setId(currentId.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        // Ψάχνει σε όλους τους χρήστες για να βρει το username
        return users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Boolean existsByUsername(String username) {
        // Ελέγχει αν υπάρχει χρήστης με αυτό το username
        return users.values().stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }

    // Η ΥΛΟΠΟΙΗΣΗ ΤΗΣ ΜΕΘΟΔΟΥ ΠΟΥ ΕΛΕΙΠΕ
    @Override
    public Boolean existsByEmail(String email) {
        // Ελέγχει αν υπάρχει χρήστης με αυτό το email
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }
}