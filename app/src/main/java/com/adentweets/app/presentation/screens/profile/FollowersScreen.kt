package com.adentweets.app.presentation.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adentweets.app.presentation.theme.AdenBlue
import androidx.navigation.NavController
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.user.GetFollowersUseCase
import com.adentweets.app.presentation.components.common.Base64AvatarView
import com.adentweets.app.presentation.components.common.XButton
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowersViewModel @Inject constructor(private val getFollowersUseCase: GetFollowersUseCase) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    fun loadFollowers(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = getFollowersUseCase(uid)) {
                is Resource.Success -> _users.value = result.data
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }
}

@Composable
fun FollowersScreen(navController: NavController, uid: String, viewModel: FollowersViewModel = hiltViewModel()) {
    val users by viewModel.users.collectAsState()
    LaunchedEffect(uid) { viewModel.loadFollowers(uid) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }
            Text("المتابِعون", style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(users, key = { it.uid }) { user ->
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).clickable { navController.navigate(Screen.Profile.createRoute(user.uid)) }, verticalAlignment = Alignment.CenterVertically) {
                    Base64AvatarView(base64Data = user.avatarBase64, size = 44.dp, modifier = Modifier.clip(CircleShape))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = user.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
                            if (user.isVerified) { Spacer(modifier = Modifier.width(4.dp)); Icon(Icons.Default.CheckCircle, "Verified", tint = AdenBlue, modifier = Modifier.size(18.dp)) }
                        }
                        Text(text = user.profileUrl, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                    XButton(text = "متابعة", onClick = {}, isSmall = true)
                }
            }
        }
    }
}