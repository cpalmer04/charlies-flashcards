package com.cpalmer.projects.flashcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlashcardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlashcardsApplication.class, args);

        /*
            ToDo

            1. Set up endpoints for
            - some kind of login page that will get the requested user that shows all of their decks
            - creating a new deck
            - getting a deck by id
               Note: how can I do this in the UI? It will have the deck names and if you click it opens up the deck
            - creating a new flashcard in a deck
            - getting a flashcard by id
               Note: same as above but I will likely have an arrow when you open up the deck to traverse flashcards
            2. Build frontend and UI

         */


    }
}