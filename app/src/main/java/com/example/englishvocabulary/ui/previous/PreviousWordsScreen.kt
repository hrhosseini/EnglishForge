package com.example.englishvocabulary.ui.previous

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Word
import com.example.englishvocabulary.ui.word.WordViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PreviousWordsScreen(
    viewModel: WordViewModel,
    onNavigateToDetail: (Word) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLevelFilter by remember { mutableStateOf<String?>(null) }
    
    // Tabs: 0 -> Bookmarked, 1 -> Suggested History
    var selectedTab by remember { mutableStateOf(0) }

    val cachedWords by viewModel.cachedWords.collectAsState()
    val syncState by viewModel.syncHistoryState.collectAsState()

    // Retrieve fresh word index from backend when accessing screen
    LaunchedEffect(Unit) {
        viewModel.syncWordsFromServer()
    }

    // Filter local entities list based on criteria
    val filteredWords = cachedWords.filter { word ->
        val matchesSearch = word.word.contains(searchQuery, ignoreCase = true) ||
                word.definition.contains(searchQuery, ignoreCase = true)
        val matchesLevel = selectedLevelFilter == null || word.cefrLevel == selectedLevelFilter
        
        val matchesTab = if (selectedTab == 0) word.isSaved else word.isSuggested

        matchesSearch && matchesLevel && matchesTab
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vocabulary Index") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.syncWordsFromServer() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync indices")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Words...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Dynamic Level Filters Horizontal Slider
            Text(
                text = "Filter Level:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
                levels.forEach { level ->
                    val isActive = selectedLevelFilter == level
                    FilterChip(
                        selected = isActive,
                        onClick = { selectedLevelFilter = if (isActive) null else level },
                        label = { Text(level) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Bookmarked") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Suggested History") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sync indicators
            if (syncState is Resource.Loading && filteredWords.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (filteredWords.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No recorded vocabulary fits this category.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredWords) { item ->
                            WordListEntry(
                                wordItem = item,
                                onClick = { onNavigateToDetail(item) },
                                onToggleSave = { viewModel.toggleSave(item.id, item.isSaved) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WordListEntry(
    wordItem: Word,
    onClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = wordItem.word,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = wordItem.cefrLevel,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "[${wordItem.partOfSpeech}]",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = wordItem.definition,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            IconButton(onClick = onToggleSave) {
                Icon(
                    imageVector = if (wordItem.isSaved) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Save Option",
                    tint = if (wordItem.isSaved) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
