# Android Passwordless Auth Assignment

This project implements a passwordless authentication flow using **Email + OTP**, built with **Jetpack Compose** and **MVVM architecture**.  
The assignment focuses on correct **OTP rule enforcement**, **state-driven UI**, and **session management**, without relying on backend services.

---

## Features

- **Email-based Login**
    - User enters an email address to initiate authentication.

- **OTP Authentication**
    - 6-digit OTP generated locally.
    - OTP expiry: **60 seconds**.
    - Maximum **3 validation attempts**.
    - Generating a new OTP invalidates the previous one and resets attempts.

- **Session Management**
    - Session start time recorded on successful login.
    - Live session duration timer (mm:ss).
    - Logout ends the session and resets authentication state.

- **Analytics & Logging**
    - Logs OTP generation, validation success/failure, and logout events using Timber.

- **Robust State Handling**
    - State is managed via ViewModel and survives recompositions and configuration changes.

---

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM (single source of truth using state)
- **Concurrency**: Kotlin Coroutines
- **Logging / Analytics**: Timber

---

## Implementation Overview

### Authentication State
- The authentication flow is driven by a single immutable state object (`AuthState`).
- UI reacts automatically to state changes using Compose recomposition.
- All business logic is centralized in `AuthViewModel`.

### OTP Logic
- OTP generation and validation are handled by `OtpManager`.
- OTP data is stored per email using an in-memory map to simulate backend behavior.
- Validation enforces:
    - Expiry time
    - Attempt limits
    - Correct OTP matching

### Session Timer
- Session duration is tracked using a coroutine in the ViewModel scope.
- Timer updates every second.
- Timer stops cleanly on logout or when the ViewModel is cleared.

### Analytics
- Timber is used for structured logging.
- An `AnalyticsLogger` wrapper is added to decouple logging from business logic and allow easy future replacement with a real analytics SDK.

---

## Setup Instructions

1. Open the project in **Android Studio**.
2. Sync Gradle files.
3. Run the app on an emulator or physical Android device (API 24+).
4. For testing, the generated OTP can be seen in **Logcat** (filter by `OTP_GENERATED`).

---

## Notes on OTP Delivery

- OTPs are generated and validated **locally**, as required by the assignment.
- No email service or backend integration is used.
- This keeps the focus on OTP rules, state handling, and application architecture.

---

## AI Assistance Disclosure

AI tools were used selectively as a productivity aid for:
- Initial Compose screen scaffolding.
- Reviewing edge cases in OTP logic.
- Verifying architectural best practices.

All **core logic, flow design, state management decisions, and debugging** were implemented and reasoned through manually.

---

### Summary

This project fulfills all assignment requirements with a focus on clean architecture, predictable state management, and clear separation of concerns.


## Demo Video

A short video demonstrating the complete flow of the application (Email → OTP → Session → Logout):

▶️ **Watch Demo Video:**  
https://drive.google.com/file/d/1NqzElXXKMBc9oioOXpKr_-_w_fIIIG8j/view?usp=sharing
