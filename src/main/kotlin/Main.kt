package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.google.GoogleModels

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    println("🤖 多 LLM 助手系統啟動中...")

    // 顯示可用的供應商
    println("📋 可用的 LLM 供應商：")
    ApiKeyManager.getAvailableProviders().forEach { provider ->
        println("   ✅ $provider")
    }

    try {
        val setup = BasicMultiLLMSetup()
        val multiExecutor = setup.createBasicMultiExecutor()

        val agent = AIAgent(
            executor = multiExecutor,
            systemPrompt = "你是一個智能助手，使用多個 LLM 供應商為使用者提供最佳服務。請用正體中文回答問題。",
//            llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
            llmModel = GoogleModels.Gemini2_5Flash
        )

        println("\n✅ 多 LLM 助手系統已就緒！")

        val question = "你好，你現在正在使用哪個模型回答問題？ 請具體回答出那一個模型"

        println("\n👤 使用者：$question")
        println("🤖 AI 回答：")
        val response = agent.run(question)
        println(response)

    } catch (e: Exception) {
        println("❌ 系統啟動失敗：${e.message}")
        e.printStackTrace()
    }
}