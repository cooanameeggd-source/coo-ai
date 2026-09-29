package com.example.data.repository

import com.example.data.local.KnowledgeDao
import com.example.data.local.KnowledgeEntity
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow

data class AiQueryResult(
    val answer: String,
    val matchedEntity: KnowledgeEntity? = null,
    val source: QuerySource
)

enum class QuerySource {
    EXACT_LOCAL,
    KEYWORD_LOCAL,
    GEMINI_AI,
    NOT_FOUND
}

class AiRepository(
    private val knowledgeDao: KnowledgeDao
) {
    fun getAllKnowledgeFlow(): Flow<List<KnowledgeEntity>> =
        knowledgeDao.getAllKnowledgeFlow()

    fun searchKnowledge(query: String): Flow<List<KnowledgeEntity>> =
        knowledgeDao.searchKnowledge(query)

    suspend fun addKnowledge(question: String, answer: String, keywords: String): Long {
        return knowledgeDao.insert(
            KnowledgeEntity(
                question = question.trim(),
                answer = answer.trim(),
                keywords = keywords.trim(),
                isSystemDefault = false
            )
        )
    }

    suspend fun updateKnowledge(entity: KnowledgeEntity) {
        knowledgeDao.update(entity)
    }

    suspend fun deleteKnowledge(id: Long) {
        knowledgeDao.deleteById(id)
    }

    suspend fun answerQuestion(
        rawQuestion: String,
        matchingMode: String = "smart" // "exact", "smart", "ai"
    ): AiQueryResult {
        val cleaned = rawQuestion.trim()
        val allLocal = knowledgeDao.getAllKnowledge()

        // 1. Exact match test (Case insensitive and trimmed)
        val exactMatch = allLocal.firstOrNull {
            it.question.trim().equals(cleaned, ignoreCase = true)
        }
        if (exactMatch != null) {
            return AiQueryResult(
                answer = exactMatch.answer,
                matchedEntity = exactMatch,
                source = QuerySource.EXACT_LOCAL
            )
        }

        // Special check for user's Python logic variations:
        // DC = "ทฤษฎีรูหนอนเป็นยังไง"
        // disapong = "เด็กที่ฉลาดที่สุด"
        if (cleaned.contains("ทฤษฎีรูหนอน") || cleaned.contains("รูหนอน")) {
            val wormhole = allLocal.firstOrNull { it.question.contains("ทฤษฎีรูหนอน") }
            if (wormhole != null) {
                return AiQueryResult(
                    answer = wormhole.answer,
                    matchedEntity = wormhole,
                    source = QuerySource.KEYWORD_LOCAL
                )
            }
        }

        if (cleaned.contains("เด็กที่ฉลาดที่สุด") || cleaned.contains("disapong") || cleaned.contains("ใครฉลาดที่สุด")) {
            val smartest = allLocal.firstOrNull { it.question.contains("เด็กที่ฉลาดที่สุด") }
            if (smartest != null) {
                return AiQueryResult(
                    answer = smartest.answer,
                    matchedEntity = smartest,
                    source = QuerySource.KEYWORD_LOCAL
                )
            }
        }

        if (cleaned.contains("อากิ้น") || cleaned.contains("akin")) {
            return AiQueryResult(
                answer = "อากิ้นฉลาดที่สุด",
                matchedEntity = null,
                source = QuerySource.KEYWORD_LOCAL
            )
        }

        // 2. Keyword & partial match in database (if mode != "exact")
        if (matchingMode != "exact") {
            val partialMatch = allLocal.firstOrNull { entity ->
                val qLower = entity.question.lowercase()
                val kLower = entity.keywords.lowercase()
                val target = cleaned.lowercase()

                target.contains(qLower) ||
                    kLower.split(",").map { it.trim() }.any { kw -> kw.isNotEmpty() && target.contains(kw) }
            }

            if (partialMatch != null) {
                return AiQueryResult(
                    answer = partialMatch.answer,
                    matchedEntity = partialMatch,
                    source = QuerySource.KEYWORD_LOCAL
                )
            }

            // 3. Fallback to Gemini AI if available
            val geminiAnswer = GeminiService.askGemini(cleaned)
            if (!geminiAnswer.isNullOrBlank()) {
                return AiQueryResult(
                    answer = geminiAnswer.trim(),
                    matchedEntity = null,
                    source = QuerySource.GEMINI_AI
                )
            }
        }

        // 4. Not found
        return AiQueryResult(
            answer = "ไม่พบข้อมูล",
            matchedEntity = null,
            source = QuerySource.NOT_FOUND
        )
    }
}
