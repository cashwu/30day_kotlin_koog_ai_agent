package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.tools
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    suspend fun main() {
        // 測試問題：讓 AI 為新咖啡店想三個店名
        val question = "請為一家新開的咖啡店推薦三個店名"

        // 低溫度 Agent：保守、穩定的回應
        val conservativeAgent = AIAgent(
            executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
            systemPrompt = "你是一個專業的品牌顧問",
            temperature = 0.1, // 極低溫度，追求穩定性
            llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
        )

        // 高溫度 Agent：創意、多樣的回應
        val creativeAgent = AIAgent(
            executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
            systemPrompt = "你是一個專業的品牌顧問",
            temperature = 1.2, // 高溫度，追求創造性
            llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
        )

        println("=== 🔒 保守型 AI (Temperature: 0.1) ===")
        val conservativeResult = conservativeAgent.run(question)
        println(conservativeResult)

        println("\n=== 🎨 創意型 AI (Temperature: 1.2) ===")
        val creativeResult = creativeAgent.run(question)
        println(creativeResult)
    }
}