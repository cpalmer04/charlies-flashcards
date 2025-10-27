package com.cpalmer.projects.flashcards.controller;

import com.cpalmer.projects.flashcards.data.*;
import com.cpalmer.projects.flashcards.entity.Deck;
import com.cpalmer.projects.flashcards.entity.Flashcard;
import com.cpalmer.projects.flashcards.entity.User;
import com.cpalmer.projects.flashcards.repository.DeckRepository;
import com.cpalmer.projects.flashcards.repository.FlashcardRepository;
import com.cpalmer.projects.flashcards.repository.UserRepository;
import com.cpalmer.projects.flashcards.security.FlashcardUserDetailsService;
import com.cpalmer.projects.flashcards.security.JwtService;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("api")
public class FlashcardsApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(FlashcardsApplicationController.class);

    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FlashcardUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final MeterRegistry meterRegistry;

    public FlashcardsApplicationController(UserRepository userRepository, DeckRepository deckRepository,
                                           FlashcardRepository flashcardRepository, PasswordEncoder passwordEncoder,
                                           JwtService jwtService, FlashcardUserDetailsService userDetailsService,
                                           AuthenticationManager authenticationManager, MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.flashcardRepository = flashcardRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/current-user")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            logger.info("Failed to get user: " + username);
            return ResponseEntity.notFound().build();
        }

        user.setPassword(null);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest userRequest) {
        try {

            User user = userRepository.findByUsername(userRequest.username());

            if (user == null) {
                logger.info("Failed to log in user: " + userRequest.username());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userRequest.username(),
                            userRequest.password()
                    )
            );

            var userDetails = userDetailsService.loadUserByUsername(userRequest.username());
            String token = jwtService.generateToken(userDetails.getUsername());

            logger.info("Logged in user: " + userRequest.username());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signupUser(@RequestBody UserRequest createUserRequest) {
        String username = createUserRequest.username();
        String password = createUserRequest.password();

        if (userRepository.findByUsername(username) != null || userRepository.count() > 30) {
            logger.info("Could not create user: " + username);
            this.meterRegistry.counter("users_created_failed").increment();
            return ResponseEntity.badRequest().build();
        }

        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(username, encodedPassword);
        userRepository.save(newUser);

        logger.info("Successfully created user: " + username);
        this.meterRegistry.counter("users_created_success").increment();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/create/deck")
    public ResponseEntity<Deck> createNewDeck(@RequestBody CreateDeckRequest createDeckRequest) {
        int userId = createDeckRequest.userId();
        String deckName = createDeckRequest.deckName();

        if (deckRepository.count() > 200) {
            logger.warn("Max deck count reached, may need to upgrade database!");
            return ResponseEntity.badRequest().build();
        }

        User associatedUser = userRepository.findByUserId(userId);
        Deck deck = new Deck(deckName, associatedUser);
        Deck savedDeck = deckRepository.save(deck);

        this.meterRegistry.counter("decks_created_success").increment();
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

        if (frontText.length() > 1000 || backText.length() > 1000) {
            logger.info("Could not create flashcard because front and back text was too long");
            return ResponseEntity.badRequest().build();
        }

        if (flashcardRepository.count() > 5000) {
            logger.info("Max flashcard count reached, may need to upgrade database!");
            return ResponseEntity.badRequest().build();
        }

        Deck deck = deckRepository.findByDeckId(deckId);
        Flashcard flashcard = new Flashcard(deck, frontText, backText);
        Flashcard savedFlashcard = flashcardRepository.save(flashcard);

        this.meterRegistry.counter("flashcards_created_success").increment();
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
