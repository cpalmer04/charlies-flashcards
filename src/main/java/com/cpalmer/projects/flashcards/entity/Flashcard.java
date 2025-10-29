package com.cpalmer.projects.flashcards.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

/**
 * Represents the flashcards table in the database.
 * Used to store the front text and back text of a singular flashcard.
 * Each flashcard is uniquely owned by one {@link Deck} in the database.
 *
 * @author cpalmer
 */
@Entity
@Table(name = "flashcards")
public class Flashcard {

    /**
     * flashcardId: The unique identifier and primary key for the flashcard
     * auto-increments in the database when a new flashcard is created
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int flashcardId;

    /**
     * frontText: The text displayed on the front of the flashcard
     * This would be the question being asked
     */
    @Column()
    private String frontText;

    /**
     * backText: The text displayed on the back of the flashcard
     * This would be the answer to the question being asked
     */
    @Column()
    private String backText;

    /**
     * deck: The deck that this flashcard belongs to
     * Many-to-one relationship with {@link Deck}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    @JsonBackReference
    private Deck deck;

    /**
     * Construct an instance
     *
     * @param deck the deck this flashcard belongs to
     * @param frontText the text (question) on the front of the flashcard
     * @param backText the text (answer) on the back of the flashcard
     */
    public Flashcard(Deck deck, String frontText, String backText) {
        this.deck = deck;
        this.frontText = frontText;
        this.backText = backText;
    }

    /**
     * Default constructor
     */
    public Flashcard() {
    }

    // Getters and setters - non javadoc due to redundancy
    public int getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(int flashcardId) {
        this.flashcardId = flashcardId;
    }

    public String getFrontText() {
        return frontText;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    public String getBackText() {
        return backText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

}
