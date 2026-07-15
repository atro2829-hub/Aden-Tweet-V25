package com.adentweets.app.presentation.screens.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.auth.LoginWithPhoneUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneVerifyViewModel @Inject constructor(
    private val loginWithPhoneUseCase: LoginWithPhoneUseCase
) : ViewModel() {
    val otpCode = MutableStateFlow("")
    val verificationId = MutableStateFlow("")
    private val _state = MutableStateFlow<Resource<User>?>(null)
    val state: StateFlow<Resource<User>?> = _state
    val isVerified = MutableStateFlow(false)

    fun verifyOtp() {
        viewModelScope.launch {
            if (otpCode.value.length == 6 && verificationId.value.isNotBlank()) {
                _state.value = Resource.Loading()
                _state.value = loginWithPhoneUseCase(verificationId.value, otpCode.value)
                if (_state.value is Resource.Success) isVerified.value = true
            }
        }
    }
}