package com.cpalmer.projects.flashcards.repository;

import com.cpalmer.projects.flashcards.entity.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Standard JPA repo interface for the flashcard information database table
 * @author cpalmer
 */
@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Integer> {
}