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

        const container = document.getElementById("currentFlashcard");
        if (this.flashcards[this.currentFlashcard] != null) {
            container.textContent = this.flashcards[this.currentFlashcard].frontText;
        } else {
            container.textContent = "Deck is empty";
        }
    },
    methods: {
        navToDeck() {
            window.location.href = 'decks.html';
        },
        flipCard() {
            if (this.currentFlashcard < this.flashcards.length) {
                const container = document.getElementById("currentFlashcard");
                if (container.textContent === this.flashcards[this.currentFlashcard].frontText) {
                    container.textContent = this.flashcards[this.currentFlashcard].backText;
                } else {
                    container.textContent = this.flashcards[this.currentFlashcard].frontText;
                }
            }
        },
        getNextCard() {
            const container = document.getElementById("currentFlashcard");
            if (this.currentFlashcard < this.flashcards.length - 1) {
                this.currentFlashcard++;
                container.textContent = this.flashcards[this.currentFlashcard].frontText;
            } else {
                this.currentFlashcard = this.flashcards.length;
                container.textContent = "Add new flashcard?";
                const plusSign = document.createElement("span");
                plusSign.textContent = "+"
                plusSign.onclick = () => this.createFlashcard();
                container.appendChild(plusSign);
            }
        },
        getPrevCard() {
            if (this.currentFlashcard !== 0) {
                this.currentFlashcard--;
                const container = document.getElementById("currentFlashcard");
                container.textContent = this.flashcards[this.currentFlashcard].frontText;
            }
        },
        createFlashcard() {
            const container = document.getElementById("createFlashcard");
            const plusSign = document.getElementById("plusSign");

            container.removeChild(plusSign);

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
                        /*container.appendChild(plusSign);
                        container.removeChild(newTextBox);
                        try {
                            // Send POST request to rest endpoint to create a new deck
                            const storedDeck = sessionStorage.getItem('currentDeck');
                            const deckObj = JSON.parse(storedDeck);
                            const storedDeckId = deckObj.deckId;
                            const response = await axios.post('http://localhost:29001/api/create/flashcard', {
                                userId: storedUserId,
                                deckName: name
                            });

                            // Update the UI to have the new deck
                            const createdDeck = response.data;
                            if (!createdDeck.flashcards) {
                                createdDeck.flashcards = [];
                            }
                            userObj.decks.push(createdDeck);
                            sessionStorage.setItem('user', JSON.stringify(userObj));
                            this.decks.push(createdDeck);
                        } catch (error) {
                            console.error('Deck creation failed:', error);
                        }*/
                    } else {
                        alert("Please enter a deck name!");
                    }
                }
            });
        }
    }
}).mount('#flashcards');
