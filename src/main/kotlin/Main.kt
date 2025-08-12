package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    try {
        val agent = AIAgent(
            executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
//            executor = simpleOpenAIExecutor("fake api key"),
            systemPrompt = "你是一個友善的 AI 助手",
            llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
        )

        // 測試是否能正常運作
        val result = agent.run("你好")
        println("✅ Agent 建立成功：$result")
    } catch (e: Exception) {
        when {
            // API 金鑰相關錯誤
            e.message?.contains("api", ignoreCase = true) == true ||
                    e.message?.contains("key", ignoreCase = true) == true ||
                    e.message?.contains("auth", ignoreCase = true) == true -> {
                println("❌ API 金鑰問題：請檢查您的 API 金鑰是否正確且有效")
            }

            // 配額相關錯誤
            e.message?.contains("quota", ignoreCase = true) == true ||
                    e.message?.contains("limit", ignoreCase = true) == true -> {
                println("⏱️ API 配額已滿：請稍後再試或檢查您的使用配額")
            }

            // 其他 API 錯誤
            else -> {
                println("原始錯誤訊息：${e.message}")
                println("❓ 無法連接到 AI 服務，請稍後再試")
            }
        }
    }
}