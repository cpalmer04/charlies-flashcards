const { createApp } = Vue;

createApp({
    data() {
        return {
            user: null,
            decks: []
        };
    },
    mounted() {
        axios.get('/api/current-user')
            .then(response => {
                this.user = response.data;
                this.decks = this.user.decks || [];
            })
            .catch(error => {
                window.location.href = 'login.html';
            });
    },
    methods: {
        openDeck(deck) {
            sessionStorage.setItem('currentDeck', JSON.stringify(deck));
            sessionStorage.setItem('deckIndex', this.decks.indexOf(deck));
            window.location.href = 'flashcards.html';
        },
        getDaysUntilReview(reviewTime) {
            const reviewDate = new Date(reviewTime);
            const now = new Date();
            const diffMs = reviewDate - now;

            const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

            return diffDays <= 0 ? 0 : diffDays;
        },
        createDeck() {
            const container = document.getElementById("newDeck");
            const plusSign = document.getElementById("plusSign");
            const newDeckText = document.getElementById("newDeckText");

            container.removeChild(plusSign);
            container.removeChild(newDeckText);

            if (document.getElementById("textbox")) return;

            const newTextBox = document.createElement("input");
            newTextBox.id = "textbox";
            newTextBox.type = "text";
            newTextBox.placeholder = "Enter deck name";
            container.appendChild(newTextBox);

            newTextBox.focus()

            newTextBox.addEventListener("keypress", async (e) => {
                if (e.key === "Enter") {
                    const name = newTextBox.value.trim();
                    if (name) {
                        container.appendChild(plusSign);
                        container.appendChild(newDeckText);
                        container.removeChild(newTextBox);
                        try {
                            // Send POST request to rest endpoint to create a new deck
                            const userId = this.user.userId
                            const response = await axios.post('http://localhost:29001/api/create/deck', {
                                userId: userId,
                                deckName: name
                            });

                            // Update the UI to have the new deck
                            const createdDeck = response.data;
                            if (!createdDeck.flashcards) {
                                createdDeck.flashcards = [];
                            }
                            this.user.decks.push(createdDeck);
                        } catch (error) {
                            console.error('Deck creation failed:', error);
                        }
                    } else {
                        alert("Please enter a deck name!");
                    }
                }
            });
        }
    }
}).mount('#decks');
