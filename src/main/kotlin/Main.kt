package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    // 建立執行器（負責與 OpenAI 溝通）
    val executor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY"))

    // 建立 AI Agent（你的 AI 助手）
    val agent = AIAgent(
        executor = executor,
        systemPrompt = "你是一個友善的 AI 助手，用正體中文回答問題",
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )

    // 開始對話！
    val response = agent.run("你好！請介紹一下自己")
    println("AI 回答：$response")
}