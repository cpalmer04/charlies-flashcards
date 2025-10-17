package com.cpalmer.projects.flashcards.data;

public record CreateFlashcardRequest(int deckId, String frontText, String backText) {
}
