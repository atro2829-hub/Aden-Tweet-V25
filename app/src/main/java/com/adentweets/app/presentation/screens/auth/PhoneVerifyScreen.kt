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
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.auth.viewmodel.PhoneVerifyViewModel

@Composable
fun PhoneVerifyScreen(
    navController: NavController,
    viewModel: PhoneVerifyViewModel = hiltViewModel()
) {
    val otpCode by viewModel.otpCode.collectAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.isVerified.value) {
        if (viewModel.isVerified.value) {
            navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
        }

        Text("تأكيد رقم هاتفك", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("أرسلنا رمزًا مكونًا من ٦ أرقام إلى هاتفك. أدخله أدناه.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))

        Spacer(modifier = Modifier.height(32.dp))
        XTextField(
            value = otpCode,
            onValueChange = { if (it.length <= 6) viewModel.otpCode.value = it.filter { c -> c.isDigit() } },
            placeholder = "أدخل رمز التحقق المكون من ٦ أرقام",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isSingleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))
        XButton(
            text = "تأكيد",
            onClick = { viewModel.verifyOtp() },
            isLoading = state is Resource.Loading,
            isFullWidth = true,
            enabled = otpCode.length == 6
        )

        if (state is Resource.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text((state as Resource.Error).message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}