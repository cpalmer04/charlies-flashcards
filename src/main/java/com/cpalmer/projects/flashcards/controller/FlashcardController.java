package com.cpalmer.projects.flashcards.controller;

import com.cpalmer.projects.flashcards.entity.Flashcard;
import com.cpalmer.projects.flashcards.repository.FlashcardRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    private FlashcardRepository flashcardRepository;

    public FlashcardController(FlashcardRepository flashcardRepository) {
        this.flashcardRepository = flashcardRepository;
    }

    @GetMapping
    public List<Flashcard> getAllFlashcards() {
        return flashcardRepository.findAll();
    }

}
