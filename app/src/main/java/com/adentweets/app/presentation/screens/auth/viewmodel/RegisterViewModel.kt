package com.adentweets.app.presentation.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.core.util.ValidationUtils
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.auth.RegisterWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerWithEmailUseCase: RegisterWithEmailUseCase
) : ViewModel() {

    val currentStep = MutableStateFlow(0)
    val email = MutableStateFlow("")
    val displayName = MutableStateFlow("")
    val username = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")
    val isPasswordVisible = MutableStateFlow(false)

    private val _registerState = MutableStateFlow<Resource<User>?>(null)
    val registerState: StateFlow<Resource<User>?> = _registerState

    private val _navigationEvent = MutableSharedFlow<RegisterNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _errors = MutableStateFlow<Map<String, String>>(emptyMap())
    val errors: StateFlow<Map<String, String>> = _errors

    fun nextStep() {
        val step = currentStep.value
        val newErrors = mutableMapOf<String, String>()

        when (step) {
            0 -> {
                if (!ValidationUtils.isValidEmail(email.value))
                    newErrors["email"] = "Please enter a valid email"
            }
            1 -> {
                val nameResult = ValidationUtils.isValidDisplayName(displayName.value)
                if (!nameResult.isValid) newErrors["displayName"] = nameResult.errorMessage ?: "Invalid name"
            }
            2 -> {
                val usernameResult = ValidationUtils.isValidUsername(username.value)
                if (!usernameResult.isValid) newErrors["username"] = usernameResult.errorMessage ?: "Invalid username"
            }
            3 -> {
                val passResult = ValidationUtils.isValidPassword(password.value)
                if (!passResult.isValid) newErrors["password"] = passResult.errorMessage ?: "Invalid password"
                if (password.value != confirmPassword.value) newErrors["confirmPassword"] = "Passwords don't match"
            }
        }

        _errors.value = newErrors
        if (newErrors.isEmpty() && currentStep.value < 3) {
            currentStep.value = step + 1
        }
    }

    fun previousStep() {
        if (currentStep.value > 0) currentStep.value -= 1
    }

    fun onRegister() {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            val result = registerWithEmailUseCase(
                email.value, password.value, displayName.value, username.value
            )
            _registerState.value = result
            if (result is Resource.Success) {
                _navigationEvent.emit(RegisterNavigationEvent.NavigateToHome)
            }
        }
    }

    fun onLoginClick() {
        viewModelScope.launch { _navigationEvent.emit(RegisterNavigationEvent.NavigateToLogin) }
    }
}

sealed class RegisterNavigationEvent {
    data object NavigateToHome : RegisterNavigationEvent()
    data object NavigateToLogin : RegisterNavigationEvent()
}