package com.example.englishvocabulary.domain.model

data class Word(
    val id: Int,
    val word: String,
    val lemma: String,
    val partOfSpeech: String,
    val cefrLevel: String, // "A1", ..., "C2"
    val definition: String,
    val exampleSentence: String,
    val collocations: List<String>,
    val synonyms: List<String>,
    val source: String,
    val isSaved: Boolean = false,
    val isSuggested: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)
