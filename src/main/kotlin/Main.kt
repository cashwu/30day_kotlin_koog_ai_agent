package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    println("🤖 容錯執行器測試啟動")

    // 建立容錯執行器
    val resilientExecutor = ResilientExecutor(
        primaryExecutor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        primaryModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        fallbackExecutor = simpleGoogleAIExecutor(ApiKeyManager.googleApiKey!!),
        fallbackModel = GoogleModels.Gemini2_5Flash
    )

    // 建立 AIAgent 使用容錯執行器
    val agent = AIAgent(
        executor = resilientExecutor,
        systemPrompt = "你是一個 AI 助手，請用正體中文回答問題",
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )

    try {
        val question = "請簡單的說明，什麼是 Kotlin 的協程"
        println("📝 問題：$question")

        val response = agent.run(question)
        println("🤖 回應：$response")

    } catch (e: Exception) {
        println("❌ 執行失敗：${e.message}")
    }
}