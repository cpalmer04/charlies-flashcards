# Charlie's Flashcards

**Charlie’s Flashcards** is a full-stack web application for creating, studying, and managing custom flashcards. It is **free, responsive, and user-friendly**, designed to work seamlessly on both desktop and mobile devices.

---

## Features

- **Custom Flashcards & Decks:**  
  Create, update, and organize your flashcards into decks.

- **Spaced Repetition & Active Recall:**  
  Get feedback on when to review decks to maximize retention.

- **Secure Authentication:**  
  User accounts and endpoints are secured with **Spring Security** and **JWT tokens**.

- **Resource Limits:**  
  Limits on users, decks, and flashcards to prevent database overload.

- **Monitoring & Logging:**  
  Metrics collected via **Prometheus** and logging handled using **Log4j2**.

---

## Motivation

I built this app because existing flashcard apps were not flexible enough:

- They were primarily mobile-only and not accessible on PC.
- Many had intrusive ads.
- They didn’t allow customized spaced repetition for optimal learning.

This project allows me to:

- Study effectively using personalized flashcards.
- Access study materials on both phone and computer.
- Share flashcards with friends pursuing similar study goals.

---

## Tech Stack

- **Backend:** Java: Spring Boot, Spring Security, JWT
- **Frontend:** HTML, CSS, JS, Vue.js
- **Database:** PostgreSQL
- **Monitoring:** Prometheus
- **Logging:** Log4j2  