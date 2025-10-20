package com.cpalmer.projects.flashcards.repository;

import com.cpalmer.projects.flashcards.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Integer> {
    Deck findByDeckId(int deckId);
}