package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import com.example.myapplication.analytics.AnalyticsLogger
import com.example.myapplication.data.OtpManager
import com.example.myapplication.data.OtpData

class AuthViewModel : ViewModel() {

    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    private val otpManager = OtpManager()
    private var timerJob: Job? = null

    fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(email = email, errorMessage = null)
    }

    fun onSendOtp() {
        if (_state.value.email.isBlank()) {
            _state.value = _state.value.copy(
                errorMessage = "Email cannot be empty"
            )
            return
        }

        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            performOtpGeneration(shouldNavigate = true)
        }
    }

    private suspend fun performOtpGeneration(shouldNavigate: Boolean) {
         // Simulate network delay for better UX
        delay(500)
        val otp = otpManager.generateOtp(_state.value.email)
        // For testing convenience, we log it. In real app, send via email.
        AnalyticsLogger.logEvent("OTP_GENERATED", mapOf("email" to _state.value.email, "otp" to otp))
        Timber.d("OTP Generated for ${state.value.email}: $otp") 

        _state.value = _state.value.copy(
            isOtpSent = if (shouldNavigate) true else _state.value.isOtpSent,
            isLoading = false,
            lastGeneratedOtp = otp
        )
    }

    fun clearOtpStatus() {
        _state.value = _state.value.copy(isOtpSent = false)
    }

    fun onVerifyOtp(otp: String) {
        if (otp.length != 6) {
             _state.value = _state.value.copy(errorMessage = "OTP must be 6 digits")
             return
        }
        
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            delay(500) // Simulate network
            val result = otpManager.validateOtp(_state.value.email, otp)
            
            when (result) {
                is OtpManager.ValidationResult.Success -> {
                    AnalyticsLogger.logEvent("LOGIN_SUCCESS", mapOf("email" to _state.value.email))
                    startSession()
                }
                is OtpManager.ValidationResult.Failure -> {
                    AnalyticsLogger.logEvent("LOGIN_FAILED", mapOf("email" to _state.value.email, "reason" to result.reason))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = result.reason
                    )
                }
            }
        }
    }
    
    fun onResendOtp() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            performOtpGeneration(shouldNavigate = false)
        }
    }

    private fun startSession() {
        val startTime = System.currentTimeMillis()
        _state.value = _state.value.copy(
            isLoggedIn = true,
            isOtpSent = false,
            isLoading = false,
            sessionStartTime = startTime,
            sessionDuration = 0L
        )

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val currentDuration = (System.currentTimeMillis() - (_state.value.sessionStartTime ?: System.currentTimeMillis())) / 1000
                _state.value = _state.value.copy(sessionDuration = currentDuration)
            }
        }
    }

    fun onLogout() {
        AnalyticsLogger.logEvent("LOGOUT", mapOf("email" to _state.value.email, "duration" to _state.value.sessionDuration))
        timerJob?.cancel()
        _state.value = AuthState() // Reset state
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun onOtpChanged(otp: String) {
        _state.value = _state.value.copy(
            enteredOtp = otp,
            otpError = null
        )
    }

}