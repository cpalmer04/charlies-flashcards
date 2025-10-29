package com.cpalmer.projects.flashcards.security;

import com.cpalmer.projects.flashcards.entity.User;
import com.cpalmer.projects.flashcards.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service to load user details for Spring Security authentication
 * The main purpose of this class is to convert the user in my database to the user class that is usable
 * by Spring Security. This is done by implementing the UserDetailsService interface and overriding
 * the loadUserByUsername method to translate the fields.
 *
 * @author cpalmer
 */
@Service
public class FlashcardUserDetailsService implements UserDetailsService {

    // The database for the user data
    private UserRepository userRepository;

    /**
     * Constructor for the FlashcardUserDetailsService
     * @param userRepository The user repository to use for database access
     */
    public FlashcardUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load a user by their username
     * This method is used to convert a user in my database to the user that is usable
     * by Spring Security needed to perform its authentication
     *
     * @param username The username to search for
     * @return The UserDetails object for the user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER") // Always going to be a user, no need for admins
                .build();
    }
}
