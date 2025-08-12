package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

/**
 *
 * @author cash.wu
 * @since 2025/08/12
 * 安全的 AI 助手 - 整合錯誤處理技術
 *
 */
class SafeAIHelper {

    fun createSafeAIAgent(): AIAgent<String, String>? {
        return try {
            AIAgent(
                executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
                systemPrompt = "你是一個友善的 AI 助手，用正體中文回答問題",
                llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
            )
        } catch (e: Exception) {
            println("建立 Agent 時發生錯誤：${e.message}")
            null
        }
    }

    suspend fun askAI(question: String): String {
        val agent = createSafeAIAgent()

        // 如果 Agent 建立失敗
        if (agent == null) {
            return "❌ AI 助手暫時無法使用，請稍後再試"
        }

        return try {
            // 使用重試機制 + 超時處理
            simpleRetry() {
                // 5秒超時
                withTimeout(5000) {
                    // delay 6 秒來模擬錯誤的情況
                    delay(6000)
                    agent.run(question)
                }
            }
        } catch (e: TimeoutCancellationException) {
            "⏰ 回應時間過長，請稍後再試"
        } catch (e: Exception) {
            "❓ 處理問題時發生錯誤，請稍後再試"
        }
    }

    suspend fun <T> simpleRetry(
        maxAttempts: Int = 3,
        delayMs: Long = 1000,
        operation: suspend () -> T
    ): T {
        repeat(maxAttempts) { attempt ->
            try {
                return operation()
            } catch (e: Exception) {
                println("嘗試 ${attempt + 1} 失敗：${e.message}")
                delay(delayMs)
            }
        }

        // 最後一次嘗試，如果失敗就讓異常拋出
        return operation()
    }
}
