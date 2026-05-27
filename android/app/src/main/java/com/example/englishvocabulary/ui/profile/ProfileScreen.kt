package com.example.englishvocabulary.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.englishvocabulary.core.Constants
import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.data.datastore.SettingsDataStore
import com.example.englishvocabulary.domain.model.User
import com.example.englishvocabulary.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userRepository: UserRepository,
    settingsDataStore: SettingsDataStore,
    initialUser: User,
    onProfileUpdated: (User) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var displayName by remember { mutableStateOf(initialUser.displayName) }
    var cefrLevel by remember { mutableStateOf(initialUser.cefrLevel) }
    val interests = listOf("work", "travel", "study", "technology", "daily life", "business", "academic English")
    val selectedInterests = remember { mutableStateListOf<String>().apply { addAll(initialUser.interests) } }

    // Backend base URL inputs
    var baseUrlText by remember { mutableStateOf("") }
    
    // Load config base url from dynamic Preference Storage on start
    LaunchedEffect(Unit) {
        baseUrlText = settingsDataStore.baseUrl.first()
    }

    var isLoading by remember { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf<String?>(null) }
    var actionError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Profile Card Details
            Text(
                text = "Edit Profile Info",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Level Choice Chips
            Text(
                text = "My Target CEFR Category:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Start)
            )
            
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
                levels.forEach { level ->
                    FilterChip(
                        selected = cefrLevel == level,
                        onClick = { cefrLevel = level },
                        label = { Text(level) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interests selection chips
            Text(
                text = "My Studying Fields:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Start)
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                interests.forEach { interest ->
                    val isSelected = selectedInterests.contains(interest)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) selectedInterests.remove(interest)
                            else selectedInterests.add(interest)
                        },
                        label = { Text(interest) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            // Backend Config Header
            Text(
                text = "FastAPI Backend Connection",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "Adjust the base URL IP to point to your FastAPI server machine.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = baseUrlText,
                onValueChange = { baseUrlText = it },
                label = { Text("API Base URL") },
                placeholder = { Text("e.g. http://10.0.2.2:8000/") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Wifi, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            // Insertion Shortcuts Quick Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { baseUrlText = Constants.DEFAULT_EMULATOR_URL },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Emulator (10.0.2.2)", style = MaterialTheme.typography.labelMedium)
                }
                
                Button(
                    onClick = { baseUrlText = Constants.DEFAULT_PHYSICAL_URL },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("LAN WIFI IP", style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(visible = actionMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (actionError) MaterialTheme.colorScheme.errorContainer 
                                         else MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = actionMessage ?: "",
                        color = if (actionError) MaterialTheme.colorScheme.onErrorContainer 
                                else MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                onClick = {
                    if (displayName.isNotBlank()) {
                        isLoading = true
                        actionMessage = null
                        coroutineScope.launch {
                            // 1. Save dynamic REST base endpoint in DataStore
                            settingsDataStore.saveBaseUrl(baseUrlText.trim())
                            
                            // 2. Synchronize Profile modifications with REST
                            userRepository.updateProfile(
                                displayName = displayName.trim(),
                                cefrLevel = cefrLevel,
                                interests = selectedInterests.toList()
                            ).collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        isLoading = true
                                    }
                                    is Resource.Success -> {
                                        isLoading = false
                                        actionError = false
                                        actionMessage = "Profile settings successfully saved!"
                                        onProfileUpdated(result.data)
                                    }
                                    is Resource.Error -> {
                                        isLoading = false
                                        actionError = true
                                        actionMessage = "Error updating backend: ${result.message ?: ' '}. (Base URL preference was updated successfully)"
                                    }
                                }
                            }
                        }
                    }
                },
                enabled = !isLoading && displayName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Settings")
                }
            }
        }
    }
}
