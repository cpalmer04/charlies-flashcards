const { createApp } = Vue;

createApp({
    data() {
        return {
            deck: null,
            flashcards: [],
            editing: false,
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
        optionsDropdown() {
            const options = document.getElementById("optionsDropdownMenu");

            if (options.innerHTML === "") {
                const deleteDeckButton = document.createElement("button");
                deleteDeckButton.onclick = () => this.confirmDeleteDeck();
                deleteDeckButton.id = "deleteDeckButton";
                deleteDeckButton.textContent = "Delete deck";
                options.appendChild(deleteDeckButton);

                const deleteFlashcardButton = document.createElement("button");
                deleteFlashcardButton.onclick = () => this.confirmDeleteFlashcard();
                deleteFlashcardButton.id = "deleteFlashcardButton";
                deleteFlashcardButton.textContent = "Delete flashcard";
                options.appendChild(deleteFlashcardButton);

                const editTextButton = document.createElement("button");
                editTextButton.onclick = () => this.editText();
                editTextButton.id = "editTextButton";
                editTextButton.textContent = "Edit text";
                options.appendChild(editTextButton);

                const markAsCompleteButton = document.createElement("button");
                markAsCompleteButton.onclick = () => this.markAsComplete();
                markAsCompleteButton.id = "markAsCompleteButton";
                markAsCompleteButton.textContent = "Mark as completed";
                options.appendChild(markAsCompleteButton);
            } else {
                options.innerHTML = "";
            }
        },
        async markAsComplete() {
            const deckId = this.deck.deckId;

            try {
                const response = await axios.put(`http://localhost:29001/api/complete/deck/${deckId}`);

                const deck = response.data;
                sessionStorage.setItem('currentDeck', JSON.stringify(deck));

                window.location.href = 'decks.html';
            } catch (error) {
                alert("Deck not found or other error occurred!");
                throw error;
            }
        },
        confirmDeleteFlashcard() {
            const options = document.getElementById("optionsDropdownMenu");
            options.innerHTML = "Are you sure?";

            const yesOption = document.createElement("button")
            yesOption.onclick = () => {
                this.deleteFlashcard();
                options.innerHTML = "";
            };
            yesOption.id = "confirmYes";
            yesOption.textContent = "Yes";
            options.appendChild(yesOption);

            const noOption = document.createElement("button")
            noOption.onclick = () => {
                options.innerHTML = "";
            };
            noOption.id = "confirmNo";
            noOption.textContent = "No";
            options.appendChild(noOption);
        },
        confirmDeleteDeck() {
            const options = document.getElementById("optionsDropdownMenu");
            options.innerHTML = "Are you sure?";

            const yesOption = document.createElement("button")
            yesOption.onclick = () => {
                this.deleteDeck();
            };
            yesOption.id = "confirmYes";
            yesOption.textContent = "Yes";
            options.appendChild(yesOption);

            const noOption = document.createElement("button")
            noOption.onclick = () => {
                options.innerHTML = "";
            };
            noOption.id = "confirmNo";
            noOption.textContent = "No";
            options.appendChild(noOption);
        },
        async deleteDeck() {
            const deckId = this.deck.deckId;

            try {
                const response = await axios.delete(`http://localhost:29001/api/delete/deck/${deckId}`);
            } catch (error) {
                alert("Deck not found or other error occurred!");
                throw error;
            }

            window.location.href = 'decks.html';
        },
        async deleteFlashcard() {
            if (this.currentFlashcard === this.flashcards.length) {
                alert("Cannot delete this flashcard!");
                return;
            }

            const flashcardId = this.flashcards[this.currentFlashcard].flashcardId;

            try {
                const response = await axios.delete(`http://localhost:29001/api/delete/flashcard/${flashcardId}`);
            } catch (error) {
                alert("Flashcard not found or other error occurred!");
                throw error;
            }

            this.flashcards.splice(this.currentFlashcard, 1);

            // Update stored deck
            const storedDeck = sessionStorage.getItem('currentDeck');
            const deckObj = JSON.parse(storedDeck);
            deckObj.flashcards.splice(this.currentFlashcard, 1);
            sessionStorage.setItem('currentDeck', JSON.stringify(deckObj));

            const container = document.getElementById("currentFlashcard");

            if (this.currentFlashcard >= this.flashcards.length) {
                this.currentFlashcard = this.flashcards.length;
                this.setupCreateFlashcard();
                return;
            }

            if (this.flashcards.length > 0) {
                container.textContent = this.flashcards[this.currentFlashcard].frontText;
            } else {
                this.setupCreateFlashcard();
            }
        },
        async editText() {
            if (this.currentFlashcard === this.flashcards.length) {
                alert("Cannot edit this flashcard!");
                return;
            }

            const container = document.getElementById("currentFlashcard");
            this.editing = true;

            if (document.getElementById("editTextbox")) return;

            let isFront;

            const options = document.getElementById("optionsDropdownMenu");
            options.innerHTML = "";

            const editTextbox = document.createElement("input");
            editTextbox.id = "editTextbox";
            editTextbox.type = "text";

            if (container.textContent.trim() === this.flashcards[this.currentFlashcard].frontText.trim()) {
                editTextbox.value = this.flashcards[this.currentFlashcard].frontText;
                isFront = true;
            } else {
                editTextbox.value = this.flashcards[this.currentFlashcard].backText;
                isFront = false;
            }

            container.textContent = "";
            container.appendChild(editTextbox);
            editTextbox.focus();

            editTextbox.addEventListener("keypress", async (editEvent) => {
                if (editEvent.key === "Enter") {
                    const text = editTextbox.value.trim();
                    const currentCard = this.flashcards[this.currentFlashcard];

                    const payload = {
                        flashcardId: currentCard.flashcardId,
                        frontText: isFront ? text : currentCard.frontText,
                        backText: isFront ? currentCard.backText : text
                    };

                    try {
                        const response = await axios.put('http://localhost:29001/api/update/flashcard', payload);
                        const updatedCard = response.data;

                        const deckIndex = sessionStorage.getItem('deckIndex');

                        const storedDeck = sessionStorage.getItem('currentDeck');
                        const deckObj = JSON.parse(storedDeck);
                        deckObj.flashcards[this.currentFlashcard] = updatedCard;
                        sessionStorage.setItem('currentDeck', JSON.stringify(deckObj));

                        this.flashcards[this.currentFlashcard] = updatedCard;

                        container.textContent = isFront ? this.flashcards[this.currentFlashcard].frontText :
                            this.flashcards[this.currentFlashcard].backText;
                    } catch (error) {
                        alert("Flashcard not found or other error occurred during update!");
                        container.textContent = isFront ? currentCard.frontText : currentCard.backText;
                        console.error(error);
                        return;
                    }
                    this.editing = false;
                }
            });
        },
        flipCard() {
            if (this.editing) {
                return;
            }

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
            this.editing = false;
            const container = document.getElementById("currentFlashcard");
            if (this.currentFlashcard < this.flashcards.length - 1) {
                this.currentFlashcard++;
                container.textContent = this.flashcards[this.currentFlashcard].frontText;
            } else {
                this.setupCreateFlashcard();
            }
        },
        getPrevCard() {
            this.editing = false;
            if (this.currentFlashcard !== 0) {
                this.currentFlashcard--;
                const container = document.getElementById("currentFlashcard");
                container.textContent = this.flashcards[this.currentFlashcard].frontText;
            }
        },
        setupCreateFlashcard() {
            const container = document.getElementById("currentFlashcard");
            this.currentFlashcard = this.flashcards.length;
            container.textContent = "Add new flashcard?";
            const plusSign = document.createElement("span");
            plusSign.textContent = "+"
            plusSign.onclick = () => this.createFlashcard();
            container.appendChild(plusSign);
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
