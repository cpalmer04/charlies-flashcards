package com.cpalmer.projects.flashcards.data;

/**
 * Record class to store information about a request to create a new flashcard deck
 * @author cpalmer
 */
public record CreateDeckRequest(int userId, String deckName) {
}
