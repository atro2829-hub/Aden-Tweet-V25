package com.adentweets.app.core.util

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): ValidationResult {
        return when {
            password.length < 8 -> ValidationResult(false, "Password must be at least 8 characters")
            !password.any { it.isDigit() } -> ValidationResult(false, "Password must contain at least one number")
            !password.any { it.isLetter() } -> ValidationResult(false, "Password must contain at least one letter")
            else -> ValidationResult(true)
        }
    }

    fun isValidUsername(username: String): ValidationResult {
        return when {
            username.length < Constants.MIN_USERNAME_LENGTH ->
                ValidationResult(false, "Username must be at least ${Constants.MIN_USERNAME_LENGTH} characters")
            username.length > Constants.MAX_USERNAME_LENGTH ->
                ValidationResult(false, "Username must be at most ${Constants.MAX_USERNAME_LENGTH} characters")
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) ->
                ValidationResult(false, "Username can only contain letters, numbers, and underscores")
            username.startsWith("_") || username.endsWith("_") ->
                ValidationResult(false, "Username cannot start or end with an underscore")
            else -> ValidationResult(true)
        }
    }

    fun isValidDisplayName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Display name cannot be empty")
            name.length > Constants.MAX_DISPLAY_NAME_LENGTH ->
                ValidationResult(false, "Display name must be at most ${Constants.MAX_DISPLAY_NAME_LENGTH} characters")
            else -> ValidationResult(true)
        }
    }

    fun isValidPostContent(content: String): ValidationResult {
        return when {
            content.isBlank() -> ValidationResult(false, "Post cannot be empty")
            content.length > Constants.MAX_POST_CHARS ->
                ValidationResult(false, "Post cannot exceed ${Constants.MAX_POST_CHARS} characters")
            else -> ValidationResult(true)
        }
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.length >= 10 && phone.all { it.isDigit() || it == '+' }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)