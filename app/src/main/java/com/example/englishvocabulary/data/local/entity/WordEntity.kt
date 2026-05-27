package com.example.englishvocabulary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.englishvocabulary.domain.model.Word

@Entity(tableName = "cache_words")
data class WordEntity(
    @PrimaryKey val id: Int,
    val word: String,
    val lemma: String,
    val partOfSpeech: String,
    val cefrLevel: String,
    val definition: String,
    val exampleSentence: String,
    val collocations: String, // Stored as comma-separated values (e.g. c1||c2)
    val synonyms: String,     // Stored as comma-separated values (e.g. s1||s2)
    val source: String,
    val isSaved: Boolean,
    val isSuggested: Boolean,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Word {
        return Word(
            id = id,
            word = word,
            lemma = lemma,
            partOfSpeech = partOfSpeech,
            cefrLevel = cefrLevel,
            definition = definition,
            exampleSentence = exampleSentence,
            collocations = if (collocations.isEmpty()) emptyList() else collocations.split("||"),
            synonyms = if (synonyms.isEmpty()) emptyList() else synonyms.split("||"),
            source = source,
            isSaved = isSaved,
            isSuggested = isSuggested,
            addedAt = addedAt
        )
    }

    companion object {
        fun fromDomain(domain: Word): WordEntity {
            return WordEntity(
                id = domain.id,
                word = domain.word,
                lemma = domain.lemma,
                partOfSpeech = domain.partOfSpeech,
                cefrLevel = domain.cefrLevel,
                definition = domain.definition,
                exampleSentence = domain.exampleSentence,
                collocations = domain.collocations.joinToString("||"),
                synonyms = domain.synonyms.joinToString("||"),
                source = domain.source,
                isSaved = domain.isSaved,
                isSuggested = domain.isSuggested,
                addedAt = domain.addedAt
            )
        }
    }
}
