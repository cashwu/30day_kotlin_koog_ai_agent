package com.cashwu

/**
 * 對話紀錄資料結構 - 用來儲存對話歷史
 */
data class ConversationRecord(
    val userMessage: String,
    val assistantResponse: String,
    val timestamp: Long = System.currentTimeMillis()
)