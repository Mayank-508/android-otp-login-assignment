package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SessionScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val state = viewModel.state.value
    
    // Safety check if we end up here without login
    if (!state.isLoggedIn) {
        onLogout() // Navigate back to login
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome", style = MaterialTheme.typography.headlineLarge)
        Text(text = state.email, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Session Started At", style = MaterialTheme.typography.labelLarge)
                val startTime = state.sessionStartTime?.let {
                    SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(it))
                } ?: "--:--:--"
                Text(text = startTime, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Duration", style = MaterialTheme.typography.labelLarge)
                val minutes = state.sessionDuration / 60
                val seconds = state.sessionDuration % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.onLogout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }
    }
}
