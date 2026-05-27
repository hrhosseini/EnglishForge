package com.example.englishvocabulary.ui.word

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Word

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DailyWordScreen(
    viewModel: WordViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.dailyWordState.collectAsState()

    // Query daily proposal on startup if empty
    LaunchedEffect(Unit) {
        if (state == null) {
            viewModel.getDailyWord()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Word Proposal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val result = state) {
                is Resource.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Proposing the perfect CEFR vocabulary card...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                is Resource.Error -> {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "OfflineError",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = result.message ?: "Could not fetch dynamic suggested word.",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { viewModel.getDailyWord() }) {
                            Text("Retry Connection")
                        }
                    }
                }
                is Resource.Success -> {
                    val wordItem = result.data
                    // Check if word is saved locally by inspecting latest cached words matching its id
                    val cachedList by viewModel.cachedWords.collectAsState()
                    val savedLocally = cachedList.find { it.id == wordItem.id }?.isSaved ?: false

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // Word header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = wordItem.word,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "[${wordItem.partOfSpeech}]",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Dynamic badge
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = wordItem.cefrLevel,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        // Definition Section
                        Text(
                            text = "Definition",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = wordItem.definition,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )

                        // Sample Sentence Section
                        Text(
                            text = "Example Sentence",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp)
                        ) {
                            Text(
                                text = "\"${wordItem.exampleSentence}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        // Collocations Section
                        if (wordItem.collocations.isNotEmpty()) {
                            Text(
                                text = "Common Collocations",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                wordItem.collocations.forEach { collocation ->
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(collocation) }
                                    )
                                }
                            }
                        }

                        // Synonyms Section
                        if (wordItem.synonyms.isNotEmpty()) {
                            Text(
                                text = "Synonyms",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                wordItem.synonyms.forEach { synonym ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(synonym) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Controls Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Bookmark toggle button
                            Button(
                                onClick = { viewModel.toggleSave(wordItem.id, savedLocally) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (savedLocally) Color(0xFFFFD700) else MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = if (savedLocally) Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = if (savedLocally) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Save Option"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (savedLocally) "Saved!" else "Save Word")
                            }

                            // Review later dismiss button
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Review Later")
                            }
                        }
                    }
                }
                else -> {
                    // Blank initialization state
                }
            }
        }
    }
}
