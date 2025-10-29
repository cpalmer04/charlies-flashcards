package com.cpalmer.projects.flashcards.data;

/**
 * Record class to store information about a request to update an existing flashcard
 * @author cpalmer
 */
public record UpdateFlashcardRequest(int flashcardId, String frontText, String backText) {
}
