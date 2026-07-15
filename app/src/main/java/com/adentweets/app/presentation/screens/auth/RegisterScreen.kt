package com.adentweets.app.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.core.util.Resource
import com.adentweets.app.presentation.components.common.XButton
import com.adentweets.app.presentation.components.common.XTextField
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.auth.viewmodel.RegisterViewModel
import com.adentweets.app.presentation.screens.auth.viewmodel.RegisterNavigationEvent
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val step by viewModel.currentStep.collectAsState()
    val email by viewModel.email.collectAsState()
    val displayName by viewModel.displayName.collectAsState()
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is RegisterNavigationEvent.NavigateToHome ->
                    navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } }
                is RegisterNavigationEvent.NavigateToLogin ->
                    navController.navigate(Screen.Login.route)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Progress indicator
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .padding(horizontal = 2.dp)
                        .then(
                            if (i <= step) Modifier.then(Modifier.background(AdenBlue))
                            else Modifier.background(MaterialTheme.colorScheme.outlineVariant)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "إنشاء حسابك",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Step content
        when (step) {
            0 -> {
                Text("الخطوة ١: البريد الإلكتروني", style = MaterialTheme.typography.labelLarge, color = AdenBlue)
                Spacer(modifier = Modifier.height(8.dp))
                XTextField(
                    value = email,
                    onValueChange = { viewModel.email.value = it },
                    placeholder = "البريد الإلكتروني",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = errors.containsKey("email"),
                    errorMessage = errors["email"]
                )
            }
            1 -> {
                Text("الخطوة ٢: الاسم", style = MaterialTheme.typography.labelLarge, color = AdenBlue)
                Spacer(modifier = Modifier.height(8.dp))
                XTextField(
                    value = displayName,
                    onValueChange = { viewModel.displayName.value = it },
                    placeholder = "الاسم",
                    isError = errors.containsKey("displayName"),
                    errorMessage = errors["displayName"]
                )
            }
            2 -> {
                Text("الخطوة ٣: اسم المستخدم", style = MaterialTheme.typography.labelLarge, color = AdenBlue)
                Spacer(modifier = Modifier.height(8.dp))
                XTextField(
                    value = username,
                    onValueChange = { viewModel.username.value = it.filter { c -> c.isLetterOrDigit() || c == '_' } },
                    placeholder = "اسم المستخدم (٤-١٥ حرفاً)",
                    isError = errors.containsKey("username"),
                    errorMessage = errors["username"]
                )
                if (username.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "@${username.lowercase()}",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            3 -> {
                Text("الخطوة ٤: كلمة المرور", style = MaterialTheme.typography.labelLarge, color = AdenBlue)
                Spacer(modifier = Modifier.height(8.dp))
                XTextField(
                    value = password,
                    onValueChange = { viewModel.password.value = it },
                    placeholder = "كلمة المرور (٨ أحرف على الأقل، رقم واحد)",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = errors.containsKey("password"),
                    errorMessage = errors["password"],
                    trailingIcon = {
                        IconButton(onClick = { viewModel.isPasswordVisible.value = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "تبديل كلمة المرور"
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                XTextField(
                    value = confirmPassword,
                    onValueChange = { viewModel.confirmPassword.value = it },
                    placeholder = "تأكيد كلمة المرور",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = errors.containsKey("confirmPassword"),
                    errorMessage = errors["confirmPassword"]
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (registerState is Resource.Error) {
            Text(
                text = (registerState as Resource.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Action buttons
        if (step < 3) {
            Row(modifier = Modifier.fillMaxWidth()) {
                XButton(
                    text = "رجوع",
                    onClick = { viewModel.previousStep() },
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                XButton(
                    text = "التالي",
                    onClick = { viewModel.nextStep() },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth()) {
                XButton(
                    text = "رجوع",
                    onClick = { viewModel.previousStep() },
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                XButton(
                    text = "إنشاء حساب",
                    onClick = { viewModel.onRegister() },
                    isLoading = registerState is Resource.Loading,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val annotatedText = buildAnnotatedString {
            append("لديك حساب بالفعل؟ ")
            withStyle(SpanStyle(color = AdenBlue, fontWeight = FontWeight.Bold)) { append("تسجيل الدخول") }
        }
        Text(
            text = annotatedText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { viewModel.onLoginClick() }
                .padding(bottom = 32.dp)
        )
    }
}