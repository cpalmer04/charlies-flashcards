package com.cpalmer.projects.flashcards.repository;

import com.cpalmer.projects.flashcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Standard JPA repo interface for the application user database table
 * @author cpalmer
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find a user by their user ID
     *
     * @deprecated now that authentication using JWT is used, use <link>findByUsername</link> instead
     * @param userId The user ID to search for
     * @return The User object if found, null otherwise
     */
    User findByUserId(int userId);

    /**
     * Find a user in the users database table by their username
     *
     * @param userName the username to search for
     * @return The User object if found, null otherwise
     */
    User findByUsername(String userName);
}