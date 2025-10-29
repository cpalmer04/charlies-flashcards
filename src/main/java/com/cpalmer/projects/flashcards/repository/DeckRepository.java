package com.cpalmer.projects.flashcards.repository;

import com.cpalmer.projects.flashcards.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Standard JPA repo interface for the flashcard decks database table
 * @author cpalmer
 */
@Repository
public interface DeckRepository extends JpaRepository<Deck, Integer> {

    /**
     * Find a flashcards deck by the associated deck id
     *
     * @param deckId The deck ID to search for
     * @return The Deck object if found, null otherwise
     */
    Deck findByDeckId(int deckId);
}