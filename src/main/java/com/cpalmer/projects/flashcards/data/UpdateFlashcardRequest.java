package com.cpalmer.projects.flashcards.data;

public record UpdateFlashcardRequest(int flashcardId, String frontText, String backText) {
}
