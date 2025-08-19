package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

/**
 *
 * @author cash.wu
 * @since 2025/08/19
 *
 */
class SecureChatBot {

    private val dataSanitizer = DataSanitizer()

    val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = """
            你是一個專業的 AI 助手
        """.trimIndent(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )

    suspend fun chat(userInput: String): String {
        // 檢查用戶輸入是否包含敏感資料
        val sanitizationResult = dataSanitizer.sanitize(userInput)

        if (sanitizationResult.hasSensitiveData) {
            println("⚠️ 檢測到敏感資料：${sanitizationResult.detectedTypes.joinToString(", ")}")
            println("為了保護您的隱私，敏感資訊已被屏蔽")
        }

        // 使用遮罩後的文字與 AI 對話
        val response = try {
            agent.run(sanitizationResult.sanitizedText)
        } catch (e: Exception) {
            "抱歉，處理您的請求時遇到問題。請稍後再試。"
        }

        return response
    }
}