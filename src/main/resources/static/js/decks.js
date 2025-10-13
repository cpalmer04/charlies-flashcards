const { createApp } = Vue;

createApp({
    data() {
        return {
            user: null,
            decks: []
        };
    },
    mounted() {
        const storedUser = sessionStorage.getItem('user');

        if (storedUser) {
            this.user = JSON.parse(storedUser);
            this.decks = this.user.decks || [];
        } else {
            sessionStorage.removeItem('user');
            window.location.href = 'login.html';
        }
    },
    methods: {
        openDeck(deck) {
            sessionStorage.setItem('currentDeck', JSON.stringify(deck));
            //window.location.href = 'flashcards.html';
        }
    }
}).mount('#decks');
