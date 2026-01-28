package com.example.myapplication.viewmodel

data class AuthState(
    val email: String = "",
    val isOtpSent: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val sessionStartTime: Long? = null,
    val sessionDuration: Long = 0L,
    val enteredOtp: String = "",
    val otpError: String? = null,
    val lastGeneratedOtp: String? = null // For debug display
)
