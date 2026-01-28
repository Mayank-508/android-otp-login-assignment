package com.example.myapplication.data

import com.example.myapplication.analytics.AnalyticsLogger

class OtpManager {

    private val otpStore = mutableMapOf<String, OtpData>()

    fun generateOtp(email: String): String {
        // Invalidate old OTP
        otpStore.remove(email)
        
        val otp = (100000..999999).random().toString()
        val expiryTime = System.currentTimeMillis() + 60_000 // 60 seconds

        otpStore[email] = OtpData(
            otp = otp,
            expiresAt = expiryTime,
            attemptsLeft = 3
        )

        AnalyticsLogger.logEvent("OTP_GENERATED", mapOf("email" to email))
        return otp
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Failure(val reason: String) : ValidationResult()
    }

    fun validateOtp(email: String, enteredOtp: String): ValidationResult {
        val data = otpStore[email]

        if (data == null) {
            AnalyticsLogger.logEvent("OTP_VALIDATION_FAILURE", mapOf("email" to email, "reason" to "No OTP found"))
            return ValidationResult.Failure("No OTP requested or OTP expired")
        }

        if (System.currentTimeMillis() > data.expiresAt) {
            otpStore.remove(email)
            AnalyticsLogger.logEvent("OTP_VALIDATION_FAILURE", mapOf("email" to email, "reason" to "Expired"))
            return ValidationResult.Failure("OTP Expired")
        }

        if (data.attemptsLeft <= 0) {
            otpStore.remove(email)
            AnalyticsLogger.logEvent("OTP_VALIDATION_FAILURE", mapOf("email" to email, "reason" to "Max attempts exceeded"))
            return ValidationResult.Failure("Max attempts exceeded. Please request a new OTP.")
        }

        return if (data.otp == enteredOtp) {
            otpStore.remove(email)
            AnalyticsLogger.logEvent("OTP_VALIDATION_SUCCESS", mapOf("email" to email))
            ValidationResult.Success
        } else {
            val newAttempts = data.attemptsLeft - 1
            if (newAttempts <= 0) {
                 otpStore.remove(email)
                 AnalyticsLogger.logEvent("OTP_VALIDATION_FAILURE", mapOf("email" to email, "reason" to "Max attempts reached with this failure"))
                 ValidationResult.Failure("Incorrect OTP. Max attempts reached.")
            } else {
                otpStore[email] = data.copy(attemptsLeft = newAttempts)
                AnalyticsLogger.logEvent("OTP_VALIDATION_FAILURE", mapOf("email" to email, "reason" to "Incorrect OTP"))
                ValidationResult.Failure("Incorrect OTP. $newAttempts attempts remaining.")
            }
        }
    }
}
