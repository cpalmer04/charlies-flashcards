package com.cpalmer.projects.flashcards.data;

/**
 * Record class to store information about a request to create a new flashcard
 * @author cpalmer
 */
public record CreateFlashcardRequest(int deckId, String frontText, String backText) {
}
