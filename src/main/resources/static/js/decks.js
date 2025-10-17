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
            sessionStorage.setItem('deckIndex', this.decks.indexOf(deck));
            window.location.href = 'flashcards.html';
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
                            const storedUser = sessionStorage.getItem('user');
                            const userObj = JSON.parse(storedUser);
                            const storedUserId = userObj.userId;
                            const response = await axios.post('http://localhost:29001/api/create/deck', {
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
                        }
                    } else {
                        alert("Please enter a deck name!");
                    }
                }
            });
        }
    }
}).mount('#decks');
