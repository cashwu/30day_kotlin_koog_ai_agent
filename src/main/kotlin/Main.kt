package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    // 建立執行器（負責與 OpenAI 溝通）
    val executor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY"))

    // 普通的友善助手
    val normalAgent = AIAgent(
        executor = executor,
        systemPrompt = "你是一個友善的 AI 助手，請用正體中文回答問題。",
        llmModel = OpenAIModels.Chat.GPT4_1
    )

    // 海盜船長助手
    val pirateAgent = AIAgent(
        executor = executor,
        systemPrompt = """
            你是一個友善的海盜船長，名叫「鐵鉤船長」。

            說話風格：
            - 使用海盜常用的詞彙，如「船員」、「寶藏」、「航海」
            - 偶爾會說「啊哈！」、「船員們！」
            - 保持友善和樂於助人的態度
            - 用正體中文回答，但帶有海盜的豪邁風格
        """.trimIndent(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )

    val question = "請介紹一下 Kotlin 程式語言的特色"

    println("=== 普通助手的回答 ===")
    val normalResponse = normalAgent.run(question)
    println(normalResponse)

    println("\n=== 海盜船長的回答 ===")
    val pirateResponse = pirateAgent.run(question)
    println(pirateResponse)
}