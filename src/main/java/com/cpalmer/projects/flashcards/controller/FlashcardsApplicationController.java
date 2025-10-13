package com.cpalmer.projects.flashcards.controller;

import com.cpalmer.projects.flashcards.data.LoginRequest;
import com.cpalmer.projects.flashcards.entity.Deck;
import com.cpalmer.projects.flashcards.entity.Flashcard;
import com.cpalmer.projects.flashcards.entity.User;
import com.cpalmer.projects.flashcards.repository.DeckRepository;
import com.cpalmer.projects.flashcards.repository.FlashcardRepository;
import com.cpalmer.projects.flashcards.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            System.out.println("User found: " + user);
            return ResponseEntity.ok(user);
        } else {
            System.out.println("No user found");
            return ResponseEntity.notFound().build();
        }
    }

    /*
     * Creates a new deck
     * A user must be signed in to have the ability to create a new deck - userId therefore is always valid
     *
     * @param userId    the ID of the user
     * @param deckName  the name of the deck to be made
     * @return          an ok response including the created deck
     */
    @PostMapping("/create/deck/{userId}/{deckName}")
    public ResponseEntity<Deck> createNewDeck(@PathVariable int userId, @PathVariable String deckName) {
        User associatedUser = userRepository.findByUserId(userId); // Should always be a valid user due to logging in
        Deck deck = new Deck(deckName, associatedUser);
        Deck savedDeck = deckRepository.save(deck);
        return ResponseEntity.ok(savedDeck);
    }

    /*
     * Creates a new flashcard in a specific deck
     * Must be navigated into a deck to create a new flashcard - deckId therefore is always valid
     *
     * @param deckId     the ID of the deck to which the flashcard belongs
     * @param frontText  the text displayed on the front of the flashcard
     * @param backText   the text displayed on the back of the flashcard
     * @return           an ok response including the created flashcard
     */
    @PostMapping("/create/flashcard/{deckId}/{frontText}/{backText}")
    public ResponseEntity<Flashcard> createFlashcard(@PathVariable int deckId, @PathVariable String frontText,
                                                     @PathVariable String backText) {
        Deck deck = deckRepository.findByDeckId(deckId);
        Flashcard flashcard = new Flashcard(deck, frontText, backText);
        Flashcard savedFlashcard = flashcardRepository.save(flashcard);
        return ResponseEntity.ok(savedFlashcard);
    }

}
