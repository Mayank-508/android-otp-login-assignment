package com.example.myapplication.data

data class OtpData(
    val otp: String,
    val expiresAt: Long,
    val attemptsLeft: Int
)
