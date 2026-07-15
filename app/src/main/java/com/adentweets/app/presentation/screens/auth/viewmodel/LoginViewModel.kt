package com.adentweets.app.presentation.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.auth.LoginWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase
) : ViewModel() {

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val isPasswordVisible = MutableStateFlow(false)

    private val _loginState = MutableStateFlow<Resource<User>>(Resource.Loading())
    val loginState: StateFlow<Resource<User>> = _loginState

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onLoginClick() {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val result = loginWithEmailUseCase(email.value, password.value)
            _loginState.value = result
            if (result is Resource.Success) {
                _navigationEvent.emit(LoginNavigationEvent.NavigateToHome)
            }
        }
    }

    fun onGoogleSignIn(idToken: String) {
        // Would call LoginWithGoogleUseCase in production
    }

    fun onForgotPasswordClick() {
        viewModelScope.launch { _navigationEvent.emit(LoginNavigationEvent.NavigateToForgotPassword) }
    }

    fun onRegisterClick() {
        viewModelScope.launch { _navigationEvent.emit(LoginNavigationEvent.NavigateToRegister) }
    }
}

sealed class LoginNavigationEvent {
    data object NavigateToHome : LoginNavigationEvent()
    data object NavigateToForgotPassword : LoginNavigationEvent()
    data object NavigateToRegister : LoginNavigationEvent()
}