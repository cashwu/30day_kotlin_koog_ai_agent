package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val mathAgent = createMathTutorAgent()
    val creativeAgent = createCreativeWriterAgent()
    val serviceAgent = createCustomerServiceAgent()

    println("=== 數學計算 (Temperature: 0.1) ===")
    println(mathAgent.run("請解釋 2x + 5 = 15 的解法"))

    println("\n=== 創意寫作 (Temperature: 1.0) ===")
    println(creativeAgent.run("請用「雨夜」作為主題，寫一個短故事的開頭"))

    println("\n=== 客服對話 (Temperature: 0.6) ===")
    println(serviceAgent.run("我的訂單還沒收到，能幫我查詢一下嗎？"))
}

fun createMathTutorAgent(): AIAgent<String, String> {
    return AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = """
            你是一個數學老師，需要提供準確的數學解答。
            請一步一步解釋計算過程，確保答案的正確性。
        """.trimIndent(),
        temperature = 0.1, // 低溫度確保答案的一致性和準確性
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )
}

// 場景 2：創意寫作助手 - 需要想像力
fun createCreativeWriterAgent(): AIAgent<String, String> {
    return AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = """
            你是一個富有想像力的創意寫作導師。
            幫助用戶發揮創意，提供多元化的寫作靈感和想法。
        """.trimIndent(),
        temperature = 1.0, // 高溫度激發創造力和多樣性
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )
}

// 場景 3：客服助手 - 需要平衡專業性與親和力
fun createCustomerServiceAgent(): AIAgent<String, String> {
    return AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = """
            你是一個專業的客服助手，既要保持專業性
            又要展現親和力，為客戶提供有幫助的服務。
        """.trimIndent(),
        temperature = 0.6, // 中等溫度平衡穩定性和靈活性
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )
}