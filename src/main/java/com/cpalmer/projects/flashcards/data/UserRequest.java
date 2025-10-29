package com.cpalmer.projects.flashcards.data;

/**
 * Record class to store information about a request to find an existing user
 * @author cpalmer
 */
public record UserRequest(String username, String password) {
}
