package com.adentweets.app.presentation.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.core.util.Resource
import com.adentweets.app.presentation.components.common.XButton
import com.adentweets.app.presentation.components.common.XTextField
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.auth.viewmodel.LoginViewModel
import com.adentweets.app.presentation.screens.auth.viewmodel.LoginNavigationEvent
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is LoginNavigationEvent.NavigateToHome ->
                    navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } }
                is LoginNavigationEvent.NavigateToForgotPassword ->
                    navController.navigate(Screen.ForgotPassword.route)
                is LoginNavigationEvent.NavigateToRegister ->
                    navController.navigate(Screen.Register.route)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "تسجيل الدخول إلى أدن توييت",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        XTextField(
            value = email,
            onValueChange = { viewModel.email.value = it },
            placeholder = "البريد الإلكتروني أو اسم المستخدم",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        XTextField(
            value = password,
            onValueChange = { viewModel.password.value = it },
            placeholder = "كلمة المرور",
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { viewModel.isPasswordVisible.value = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "نسيت كلمة المرور؟",
            color = AdenBlue,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { viewModel.onForgotPasswordClick() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        XButton(
            text = "تسجيل الدخول",
            onClick = { viewModel.onLoginClick() },
            isLoading = loginState is Resource.Loading,
            isFullWidth = true
        )

        if (loginState is Resource.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (loginState as Resource.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(modifier = Modifier.height(16.dp))

        XButton(
            text = "المتابعة مع جوجل",
            onClick = { /* Google Sign In */ },
            isPrimary = false,
            isFullWidth = true
        )

        Spacer(modifier = Modifier.weight(1f))

        val annotatedText = buildAnnotatedString {
            append("ليس لديك حساب؟ ")
            withStyle(SpanStyle(color = AdenBlue, fontWeight = FontWeight.Bold)) {
                append("إنشاء حساب")
            }
        }
        Text(
            text = annotatedText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { viewModel.onRegisterClick() }
                .padding(bottom = 32.dp)
        )
    }
}