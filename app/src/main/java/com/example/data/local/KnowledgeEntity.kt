package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_items")
data class KnowledgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val answer: String,
    val keywords: String = "",
    val isSystemDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
