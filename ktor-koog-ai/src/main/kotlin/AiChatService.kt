package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.serialization.Serializable

class AiChatService(apiKey: String) {

    // 建立 OpenAI 執行器
    private val executor = simpleOpenAIExecutor(apiKey)

    // 建立 AI Agent
    private val aiAgent = AIAgent(
        executor = executor,
        systemPrompt = """
            你是一個友善且專業的 AI 助手
            請用正體中文回答使用者的問題
            保持回答簡潔明瞭，但要有幫助性
        """.trimIndent(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )

    /**
     * 處理聊天請求
     */
    suspend fun chat(chatRequest: ChatRequest): ChatResponse {
        val startTime = System.currentTimeMillis()

        return try {
            val response = aiAgent.run(chatRequest.message)
            val processingTime = System.currentTimeMillis() - startTime

            ChatResponse(
                success = true,
                message = response,
                processingTimeMs = processingTime
            )
        } catch (e: Exception) {
            ChatResponse(
                success = false,
                message = "抱歉，發生錯誤：${e.message}",
                processingTimeMs = System.currentTimeMillis() - startTime,
                error = e.message
            )
        }
    }
}

// 資料類別
@Serializable
data class ChatRequest(
    val message: String
)

@Serializable
data class ChatResponse(
    val success: Boolean,
    val message: String,
    val processingTimeMs: Long,
    val error: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)