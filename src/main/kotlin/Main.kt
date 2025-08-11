package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 註冊工具
    val toolRegistry = ToolRegistry {
        tool(SayToUser)
        tool(AddTool)
    }

    val agent = AIAgent(
        executor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")),
        systemPrompt = """
            你是一個數學助手。當使用者需要計算兩個數字相加時：
            1. 使用 add_numbers 工具進行計算
            2. 使用 SayToUser 工具告訴使用者結果

            請用友善的正體中文回應
        """.trimIndent(),
        toolRegistry = toolRegistry,
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )

    // 測試加法功能
    agent.run("請幫我計算 15 + 27")
}