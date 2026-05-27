package com.example.englishvocabulary.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.example.englishvocabulary.domain.model.Word

@JsonClass(generateAdapter = true)
data class CustomWordRequest(
    @Json(name = "word") val word: String
)

@JsonClass(generateAdapter = true)
data class WordDto(
    @Json(name = "id") val id: Int,
    @Json(name = "word") val word: String,
    @Json(name = "lemma") val lemma: String?,
    @Json(name = "partOfSpeech") val partOfSpeech: String,
    @Json(name = "cefrLevel") val cefrLevel: String,
    @Json(name = "definition") val definition: String,
    @Json(name = "exampleSentence") val exampleSentence: String,
    @Json(name = "collocations") val collocations: List<String>?,
    @Json(name = "synonyms") val synonyms: List<String>?,
    @Json(name = "source") val source: String?
) {
    fun toDomain(isSaved: Boolean = false, isSuggested: Boolean = false): Word {
        return Word(
            id = id,
            word = word,
            lemma = lemma ?: word,
            partOfSpeech = partOfSpeech,
            cefrLevel = cefrLevel,
            definition = definition,
            exampleSentence = exampleSentence,
            collocations = collocations ?: emptyList(),
            synonyms = synonyms ?: emptyList(),
            source = source ?: "global",
            isSaved = isSaved,
            isSuggested = isSuggested
        )
    }
}
