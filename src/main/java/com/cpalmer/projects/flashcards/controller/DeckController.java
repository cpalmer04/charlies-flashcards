package com.cpalmer.projects.flashcards.controller;

import com.cpalmer.projects.flashcards.entity.Deck;
import com.cpalmer.projects.flashcards.repository.DeckRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/decks")
public class DeckController {

    private final DeckRepository deckRepository;

    public DeckController(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }

    @GetMapping
    public List<Deck> getAllDecks() {
        return deckRepository.findAll();
    }
}
