package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import jdk.internal.agent.resources.agent
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withTimeout
import kotlin.run

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = "你是一個友善的 AI 助手",
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )

    try {
        // 設定 5 秒超時
        withTimeout(5000) {

            val result = agent.run("你好")
            // delay 6 秒來模型錯誤的情況
            delay(6000)
            println("✅ Agent 建立成功：$result")
        }
    } catch (e: TimeoutCancellationException) {
        println("⏰ 回應時間過長，請檢查網路連線後再試")
    } catch (e: Exception) {
        when {
            // 網路連線問題
            e.message?.contains("network", ignoreCase = true) == true ||
                    e.message?.contains("connection", ignoreCase = true) == true ||
                    e.message?.contains("timeout", ignoreCase = true) == true -> {
                println("🌐 網路連線問題，請檢查網路設定後再試")
            }

            // 服務不可用
            e.message?.contains("service", ignoreCase = true) == true ||
                    e.message?.contains("unavailable", ignoreCase = true) == true -> {
                 println("🚫 AI 服務暫時不可用，請稍後再試")
            }

            else -> {
                println("網路錯誤詳情：${e.message}") // 開發時用於除錯
                println("❓ 處理請求時發生問題，請稍後再試")
            }
        }
    }
}