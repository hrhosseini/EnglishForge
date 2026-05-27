package com.example.englishvocabulary.ui.review

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel,
    onBack: () -> Unit
) {
    val reviewQueue by viewModel.cachedDueReviews.collectAsState()
    val syncState by viewModel.syncReviewsState.collectAsState()
    val answerState by viewModel.answerState.collectAsState()

    var showDefinition by remember { mutableStateOf(false) }
    var currentCardIndex by remember { mutableStateOf(0) }

    // Sync reviews from server on startup
    LaunchedEffect(Unit) {
        viewModel.syncReviewsFromServer()
    }

    // Reset card expansion when we navigate cards
    LaunchedEffect(currentCardIndex) {
        showDefinition = false
        viewModel.clearAnswerState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spaced Repetition Review") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.syncReviewsFromServer() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh reviews cache")
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
            if (syncState is Resource.Loading && reviewQueue.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Fetching due memory cards...")
                }
            } else if (reviewQueue.isEmpty() || currentCardIndex >= reviewQueue.size) {
                // Done State
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "All done",
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Review Queue Clear!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "You've successfully answered all outstanding vocabulary due cards. Check back later!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                    )
                    Button(onClick = { viewModel.syncReviewsFromServer() }) {
                        Text("Re-Check Queue")
                    }
                }
            } else {
                val currentReview = reviewQueue[currentCardIndex]
                
                // Active Card Container
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Queue length counter
                    Text(
                        text = "Card ${currentCardIndex + 1} of ${reviewQueue.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Flip Card visual container
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (showDefinition) MaterialTheme.colorScheme.tertiaryContainer 
                                             else MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clickable { showDefinition = !showDefinition },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!showDefinition) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = currentReview.word,
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "[${currentReview.partOfSpeech}]",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Tap to Flip Card", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Definition",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = currentReview.definition,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Answer Options Section (visible on flip)
                    AnimatedVisibility(visible = showDefinition) {
                        Column {
                            Text(
                                text = "How well did you know this word?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .padding(bottom = 12.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ScoreButton(
                                    label = "Again",
                                    color = MaterialTheme.colorScheme.error,
                                    enabled = answerState !is Resource.Loading,
                                    onClick = { viewModel.submitAnswer(currentReview.wordId, "again") }
                                )
                                ScoreButton(
                                    label = "Hard",
                                    color = MaterialTheme.colorScheme.secondary,
                                    enabled = answerState !is Resource.Loading,
                                    onClick = { viewModel.submitAnswer(currentReview.wordId, "hard") }
                                )
                                ScoreButton(
                                    label = "Good",
                                    color = MaterialTheme.colorScheme.primary,
                                    enabled = answerState !is Resource.Loading,
                                    onClick = { viewModel.submitAnswer(currentReview.wordId, "good") }
                                )
                                ScoreButton(
                                    label = "Easy",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    enabled = answerState !is Resource.Loading,
                                    onClick = { viewModel.submitAnswer(currentReview.wordId, "easy") }
                                )
                            }
                        }
                    }

                    if (answerState is Resource.Loading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Simple skip option
                    TextButton(onClick = { currentCardIndex++ }) {
                        Text("Skip Card")
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.ScoreButton(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}
