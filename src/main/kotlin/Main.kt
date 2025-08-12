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
        simpleRetry(maxAttempts = 3, delayMs = 2000) {
            val result= agent.run("你好")
            // 直接丟出 error 來模擬未知的錯誤
            throw Exception("unknown error")
            println("✅ Agent 建立成功：$result")
        }
    } catch (e: Exception) {
        println("❌ 經過多次嘗試後仍無法處理您的請求，請稍後再試")
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