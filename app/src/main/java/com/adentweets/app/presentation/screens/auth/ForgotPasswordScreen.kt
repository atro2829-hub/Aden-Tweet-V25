package com.adentweets.app.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.core.util.Resource
import com.adentweets.app.presentation.components.common.XButton
import com.adentweets.app.presentation.components.common.XTextField
import com.adentweets.app.presentation.screens.auth.viewmodel.ForgotPasswordViewModel
import com.adentweets.app.presentation.theme.AdenGreen

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsState()
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
        }

        Text("إعادة تعيين كلمة المرور", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "أدخل بريدك الإلكتروني وسنرسل لك رابطاً لإعادة تعيين كلمة المرور.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))
        XTextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            placeholder = "البريد الإلكتروني",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(24.dp))

        XButton(
            text = "إرسال رابط إعادة التعيين",
            onClick = { viewModel.sendResetEmail() },
            isLoading = state is Resource.Loading,
            isFullWidth = true
        )

        if (state is Resource.Success) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("تم إرسال رابط إعادة التعيين! تحقق من بريدك الإلكتروني.", color = AdenGreen, style = MaterialTheme.typography.bodyLarge)
        }
        if (state is Resource.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text((state as Resource.Error).message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}