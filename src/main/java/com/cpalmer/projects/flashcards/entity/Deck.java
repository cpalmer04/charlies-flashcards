package com.cpalmer.projects.flashcards.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "decks")
public class Deck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int deckId;

    @Column(nullable = false)
    private String deckName;

    @Column
    private LocalDateTime nextReviewDate;

    @Column
    private Integer reviewInterval;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Flashcard> flashcards;

    public Deck(String deckName, User user) {
        this.deckName = deckName;
        this.user = user;
    }

    public Deck() {
    }

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
