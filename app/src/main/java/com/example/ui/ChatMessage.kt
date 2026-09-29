package com.example.ui

import com.example.data.repository.QuerySource

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val source: QuerySource = QuerySource.EXACT_LOCAL,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AppTab(val titleTh: String) {
    CHAT("แชท AI"),
    KNOWLEDGE("คลังความรู้"),
    PYTHON_LAB("ทดสอบ Python"),
    SETTINGS("ตั้งค่า")
}
