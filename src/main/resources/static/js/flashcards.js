const { createApp } = Vue;

createApp({
    data() {
        return {
            deck: null,
            flashcards: [],
            currentFlashcard: 0
        };
    },
    mounted() {
        const storedDeck = sessionStorage.getItem('currentDeck');

        if (storedDeck) {
            this.deck = JSON.parse(storedDeck);
            this.flashcards = this.deck.flashcards || [];
        } else {
            sessionStorage.removeItem('currentDeck');
            window.location.href = 'decks.html';
        }

        /*const container = document.getElementById("currentFlashcard");
        if (this.flashcards[this.currentFlashcard] != null) {
            container.textContent = this.flashcards[this.currentFlashcard].frontText;
        }*/
    },
    methods: {
    }
}).mount('#flashcards');
