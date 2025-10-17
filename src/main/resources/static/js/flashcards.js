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
            this.getNextCard();
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
            } else {
                this.createFlashcard();
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
            const container = document.getElementById("currentFlashcard");
            const plusSign = document.getElementById("plusSign");

            container.textContent = "";

            if (document.getElementById("textbox")) return;

            const frontTextbox = document.createElement("input");
            frontTextbox.id = "frontTextbox";
            frontTextbox.type = "text";
            frontTextbox.placeholder = "Enter text for front of card";
            container.appendChild(frontTextbox);

            frontTextbox.focus()

            frontTextbox.addEventListener("keypress", async (frontEvent) => {
                if (frontEvent.key === "Enter") {
                    const front = frontTextbox.value.trim();
                    if (!front) {
                        alert("Enter a front text");
                    } else {
                        container.removeChild(frontTextbox);
                        const backTextbox = document.createElement("input");
                        backTextbox.id = "backTextbox";
                        backTextbox.type = "text";
                        backTextbox.placeholder = "Enter text for back of card";
                        container.appendChild(backTextbox);
                        backTextbox.focus();

                        backTextbox.addEventListener("keypress", async (backEvent) => {
                            if (backEvent.key === "Enter") {
                                const back = backTextbox.value.trim();
                                if (!back) {
                                    alert("Enter a back text")
                                } else {
                                    try {
                                        const storedDeck = sessionStorage.getItem('currentDeck');
                                        const deckObj = JSON.parse(storedDeck);
                                        const storedDeckId = deckObj.deckId;
                                        const response = await axios.post('http://localhost:29001/api/create/flashcard', {
                                            deckId: storedDeckId,
                                            frontText: front,
                                            backText: back
                                        });
                                        container.removeChild(backTextbox);
                                        const createdFlashcard = response.data;
                                        deckObj.flashcards.push(createdFlashcard);

                                        const storedUser = sessionStorage.getItem('user');
                                        const userObj = JSON.parse(storedUser);
                                        const deckIndex = sessionStorage.getItem('deckIndex');
                                        userObj.decks[deckIndex] = deckObj;
                                        sessionStorage.setItem('user',  JSON.stringify(userObj));

                                        sessionStorage.setItem('currentDeck', JSON.stringify(deckObj));
                                        this.flashcards.push(createdFlashcard);
                                    } catch (error) {
                                        console.error('Flashcard creation failed:', error);
                                    }
                                    container.textContent = this.flashcards[this.currentFlashcard].frontText;
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}).mount('#flashcards');
