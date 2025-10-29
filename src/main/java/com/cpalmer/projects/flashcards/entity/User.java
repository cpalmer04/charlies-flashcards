package com.cpalmer.projects.flashcards.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

/**
 * Represents the users table in the database.
 * Used to store the login credentials of a user as well as the decks that they own.
 *
 * @author cpalmer
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * userId: The unique identifier and primary key for the user
     * auto-increments in the database when a new user is created
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    /**
     * username: The unique username of the user
     * must be unique, otherwise, throw an error
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * password: The password of the user
     * The password is intended to be hashed using {@link BCryptPasswordEncoder} when writing to the databaase.
     * Use the following line, or something similar to it, to properly store the password in the DB.
     * <code>String encodedPassword = passwordEncoder.encode(password)</code>
     * De-hashing is not needed. Spring security does not dehash. Password comparison is handled by
     * {@link DaoAuthenticationProvider} in Spring Security.
     * To do this, create a new {@link UsernamePasswordAuthenticationToken} with the provided user username and
     * password and use the provided authentication manager to call the authenticate method.
     */
    @Column(nullable = false)
    private String password;

    /**
     * decks: The list of decks owned by the user
     * One-to-many relationship with {@link Deck}
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Deck> decks;

    /**
     * Construct an instance
     *
     * @param username The username of the user
     * @param password The password of the user
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Default constructor
     */
    public User() {
    }

    // Getters and setters - non javadoc due to redundancy
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Deck> getDecks() {
        return decks;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

}
