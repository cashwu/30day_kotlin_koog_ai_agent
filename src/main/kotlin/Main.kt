package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.ExitTool
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 註冊工具
    val toolRegistry = ToolRegistry {
        tool(SayToUser)
        tool(AskUser)
        tool(ExitTool)
    }

    val agent = AIAgent(
        executor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")),
//        systemPrompt = "請使用 AskUser 詢問使用者的姓名，然後用 SayToUser 打招呼。",
        systemPrompt = "請先詢問使用者的姓名，然後在跟使用者打招呼。",
        toolRegistry = toolRegistry,
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )

    agent.run("你好！")
}