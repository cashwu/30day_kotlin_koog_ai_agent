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

    // 註冊工具
    val toolRegistry = ToolRegistry {
        tool(SayToUser)
//        tool(AddTool)
        tools(MathToolSet())
    }

    val agent = AIAgent(
        executor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")),
        systemPrompt = """
            你是一個數學助手。你有一些數學工具可以使用
            請用友善的正體中文回應
        """.trimIndent(),
        toolRegistry = toolRegistry,
        llmModel = OpenAIModels.Chat.GPT4_1
    )

    // 測試加法功能
    agent.run("請幫我計算 25 + 17")
    // 測試乘法功能
    agent.run("請幫我計算 4 * 5")
    // 測試質數功能
    agent.run("請問一下 5 是不是質數")
}