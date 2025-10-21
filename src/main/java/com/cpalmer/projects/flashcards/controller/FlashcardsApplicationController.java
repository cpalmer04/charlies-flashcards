package com.cpalmer.projects.flashcards.controller;

import com.cpalmer.projects.flashcards.data.CreateDeckRequest;
import com.cpalmer.projects.flashcards.data.CreateFlashcardRequest;
import com.cpalmer.projects.flashcards.data.LoginRequest;
import com.cpalmer.projects.flashcards.data.UpdateFlashcardRequest;
import com.cpalmer.projects.flashcards.entity.Deck;
import com.cpalmer.projects.flashcards.entity.Flashcard;
import com.cpalmer.projects.flashcards.entity.User;
import com.cpalmer.projects.flashcards.repository.DeckRepository;
import com.cpalmer.projects.flashcards.repository.FlashcardRepository;
import com.cpalmer.projects.flashcards.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("api")
public class FlashcardsApplicationController {

    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;

    public FlashcardsApplicationController(UserRepository userRepository, DeckRepository deckRepository,
                                           FlashcardRepository flashcardRepository) {
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.flashcardRepository = flashcardRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<User> getUserByIdAndPassword(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUserNameAndUserPasswordHash(
                loginRequest.username(), loginRequest.password()
        );

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create/deck")
    public ResponseEntity<Deck> createNewDeck(@RequestBody CreateDeckRequest createDeckRequest) {
        int userId = createDeckRequest.userId();
        String deckName = createDeckRequest.deckName();

        User associatedUser = userRepository.findByUserId(userId);
        Deck deck = new Deck(deckName, associatedUser);
        Deck savedDeck = deckRepository.save(deck);

        return ResponseEntity.ok(savedDeck);
    }

    @DeleteMapping("/delete/deck/{deckId}")
    public ResponseEntity<Void> deleteDeck(@PathVariable int deckId) {
        if (!deckRepository.existsById(deckId)) {
            return ResponseEntity.notFound().build();
        }

        deckRepository.deleteById(deckId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/complete/deck/{deckId}")
    public ResponseEntity<Deck> completeDeck(@PathVariable int deckId) {
        Optional<Deck> deck = deckRepository.findById(deckId);

        if (deck.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Deck newDeck = deck.get();

        Integer reviewInterval = newDeck.getReviewInterval();

        Integer nextReview;
        if (reviewInterval == null) {
            nextReview = 1;
        } else if (reviewInterval == 1) {
            nextReview = 3;
        } else if (reviewInterval == 3) {
            nextReview = 7;
        } else if (reviewInterval == 7) {
            nextReview = 14;
        } else {
            nextReview = 21;
        }

        newDeck.setReviewInterval(nextReview);
        newDeck.setNextReviewDate(LocalDateTime.now().plusDays(nextReview));

        Deck updated = deckRepository.save(newDeck);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/create/flashcard")
    public ResponseEntity<Flashcard> createFlashcard(@RequestBody CreateFlashcardRequest createFlashcardRequest) {
        int deckId = createFlashcardRequest.deckId();
        String frontText = createFlashcardRequest.frontText();
        String backText = createFlashcardRequest.backText();

        Deck deck = deckRepository.findByDeckId(deckId);
        Flashcard flashcard = new Flashcard(deck, frontText, backText);
        Flashcard savedFlashcard = flashcardRepository.save(flashcard);

        return ResponseEntity.ok(savedFlashcard);
    }

    @DeleteMapping("/delete/flashcard/{flashcardId}")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable int flashcardId) {
        if (!flashcardRepository.existsById(flashcardId)) {
            return ResponseEntity.notFound().build();
        }

        flashcardRepository.deleteById(flashcardId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/flashcard")
    public ResponseEntity<Flashcard> updateFlashcard(@RequestBody UpdateFlashcardRequest updateFlashcardRequest) {
        int flashcardId = updateFlashcardRequest.flashcardId();
        String frontText = updateFlashcardRequest.frontText();
        String backText = updateFlashcardRequest.backText();

        Optional<Flashcard> flashcard = flashcardRepository.findById(flashcardId);

        if (flashcard.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Flashcard newFlashcard = flashcard.get();
        newFlashcard.setFrontText(frontText);
        newFlashcard.setBackText(backText);

        Flashcard updated = flashcardRepository.save(newFlashcard);
        return ResponseEntity.ok(updated);
    }

}
