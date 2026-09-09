package com.palashsaathi.app.data

import android.content.Context
import android.util.Log
import com.palashsaathi.app.data.model.CorpusSentence
import com.palashsaathi.app.engine.OlChikiConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * High-performance offline repository for the 20,000-sentence Santali-English parallel corpus.
 * Loads directly from app assets (santali_corpus.csv) and provides instant bidirectional search.
 */
object SantaliCorpusRepository {

    private const val TAG = "SantaliCorpusRepo"
    private const val ASSET_NAME = "santali_corpus.csv"

    private val _sentences = mutableListOf<CorpusSentence>()
    val sentences: List<CorpusSentence> get() = _sentences

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    private var isLoading = false

    /**
     * Initializes the corpus asynchronously in background thread.
     */
    suspend fun initialize(context: Context) = withContext(Dispatchers.IO) {
        if (_isLoaded.value || isLoading) return@withContext
        isLoading = true

        try {
            val assetStream = context.assets.open(ASSET_NAME)
            val reader = BufferedReader(InputStreamReader(assetStream, Charsets.UTF_8))

            val loadedList = mutableListOf<CorpusSentence>()
            var line: String?
            var lineCount = 0

            // Skip header if present
            line = reader.readLine()

            while (reader.readLine().also { line = it } != null) {
                lineCount++
                val curLine = line ?: continue
                val parsed = parseCsvLine(curLine) ?: continue

                val id = parsed.first
                val english = parsed.second
                val santaliOlChiki = parsed.third

                if (english.isNotBlank() && santaliOlChiki.isNotBlank()) {
                    val devanagari = OlChikiConverter.toDevanagari(santaliOlChiki)
                    val phonetic = OlChikiConverter.toPhonetic(santaliOlChiki)
                    loadedList.add(
                        CorpusSentence(
                            id = id,
                            english = english,
                            santaliOlChiki = santaliOlChiki,
                            santaliDevanagari = devanagari,
                            santaliPhonetic = phonetic
                        )
                    )
                }

                // Safety limit if memory is constrained on 1GB Android Go devices
                if (loadedList.size >= 20000) break
            }

            reader.close()
            assetStream.close()

            _sentences.clear()
            _sentences.addAll(loadedList)
            _isLoaded.value = true
            Log.d(TAG, "Successfully loaded ${_sentences.size} sentences from Santali corpus.")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Santali corpus from assets", e)
        } finally {
            isLoading = false
        }
    }

    /**
     * Parses a CSV row formatted as: ID,English,Santali
     * Handles commas inside quoted text.
     */
    private fun parseCsvLine(line: String): Triple<Int, String, String>? {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false

        for (ch in line) {
            when {
                ch == '"' -> inQuotes = !inQuotes
                ch == ',' && !inQuotes -> {
                    tokens.add(sb.toString().trim())
                    sb.clear()
                }
                else -> sb.append(ch)
            }
        }
        tokens.add(sb.toString().trim())

        if (tokens.size < 3) return null

        val id = tokens[0].toIntOrNull() ?: 0
        val english = cleanText(tokens[1])
        val santali = cleanText(tokens[2])

        return Triple(id, english, santali)
    }

    private fun cleanText(text: String): String {
        var res = text.trim()
        if (res.startsWith("\"") && res.endsWith("\"") && res.length >= 2) {
            res = res.substring(1, res.length - 1)
        }
        return res.trim()
    }

    /**
     * Fast search across English, Ol Chiki, and Romanized phonetics.
     */
    fun search(query: String, limit: Int = 30): List<CorpusSentence> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return getFeaturedPhrases(limit)

        return _sentences.asSequence()
            .filter {
                it.english.lowercase().contains(q) ||
                it.santaliOlChiki.contains(query) ||
                it.santaliPhonetic.lowercase().contains(q) ||
                it.santaliDevanagari.contains(query)
            }
            .take(limit)
            .toList()
    }

    /**
     * Finds the closest match for live speech or text translation.
     */
    fun findBestMatch(query: String): CorpusSentence? {
        val q = query.trim().lowercase()
        if (q.isBlank()) return null

        // Exact match first
        val exact = _sentences.firstOrNull { it.english.lowercase() == q }
        if (exact != null) return exact

        // Prefix or substring match
        return _sentences.firstOrNull { it.english.lowercase().contains(q) || it.santaliOlChiki.contains(query) }
    }

    /**
     * Returns curated pedagogical, classroom, and foundational sentences from the corpus.
     */
    fun getFeaturedPhrases(limit: Int = 20): List<CorpusSentence> {
        val keywords = listOf("book", "school", "read", "write", "learn", "water", "tree", "child", "one", "two")
        return _sentences.asSequence()
            .filter { sentence ->
                val lower = sentence.english.lowercase()
                keywords.any { k -> lower.contains(k) }
            }
            .take(limit)
            .toList()
            .ifEmpty { _sentences.take(limit) }
    }
}
