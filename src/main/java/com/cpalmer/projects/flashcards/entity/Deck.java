package com.cpalmer.projects.flashcards.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the decks table in the database.
 * Used to group a common set of flashcards together under a single topic or subject.
 * Each deck is uniquely owned by one {@link User} in the database.
 *
 * @author cpalmer
 */
@Entity
@Table(name = "decks")
public class Deck {

    /**
     * deckId: The unique identifier and primary key for the deck
     * auto-increments in the database when a new deck is created
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int deckId;

    /**
     * deckName: The name of the deck that groups together a common set of flashcards
     * Cannot be null
     */
    @Column(nullable = false)
    private String deckName;

    /**
     * nextReviewDate: The date and time when the deck is suggested to be reviewed next
     * Can be null if the deck has not been reviewed yet
     * Intended to be used with a spaced repetition algorithm
     */
    @Column
    private LocalDateTime nextReviewDate;

    /**
     * reviewInterval: The interval that the last review date was incremented by
     * Can be null if the deck has not been reviewed yet
     * When the next review date is set, the number of days between now and that date is stored in
     * the review interval. This review interval should be incremented based on the spaced repetition algorithm
     * in order to properly set the next review date after each review. Without the review interval, the nextReviewDate
     * would be incremented by the same amount each time. Therefore, by using this, we can space out review dates
     * more effectively.
     */
    @Column
    private Integer reviewInterval;

    /**
     * user: The user that owns this deck
     * Many-to-one relationship with {@link User}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    /**
     * flashcards: The list of flashcards that belong to this deck
     * One-to-many relationship with {@link Flashcard}
     */
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Flashcard> flashcards;

    /**
     * Construct an instance
     *
     * @param deckName The name of the deck
     * @param user     The user that owns this deck
     */
    public Deck(String deckName, User user) {
        this.deckName = deckName;
        this.user = user;
    }

    /**
     * Default constructor
     */
    public Deck() {
    }

    // Getters and setters - non javadoc due to redundancy
    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    public LocalDateTime getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(LocalDateTime nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public Integer getReviewInterval() {
        return reviewInterval;
    }

    public void setReviewInterval(Integer reviewInterval) {
        this.reviewInterval = reviewInterval;
    }

}
