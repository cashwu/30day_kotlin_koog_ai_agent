package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.singleRunStrategy
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 建立整合了 Context7 MCP 工具的 AI Agent
    val agent = createContext7Agent(ApiKeyManager.openAIApiKey!!)

    val query = "如何在 Koog 中整合 MCP 工具？"

    try {
        println("\n正在查詢... $query")
        val response = agent.run(query)
        println("\n回應: $response")
    } catch (e: Exception) {
        println("\n錯誤: ${e.message}")
    }
}


/**
 * 建立整合 Context7 的 AI Agent
 */
suspend fun createContext7Agent(apiKey: String): AIAgent<String, String> {
    return AIAgent(
        executor = simpleOpenAIExecutor(apiKey),
        strategy = singleRunStrategy(),
        systemPrompt = createSystemPrompt(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        toolRegistry = createToolRegistry()
    )
}

/**
 * 建立包含 Context7 MCP 工具的註冊表
 */
suspend fun createToolRegistry(): ToolRegistry {
    // 基礎工具
    val basicTools = ToolRegistry {
        tool(SayToUser)
        tool(AskUser)
    }

    // Context7 MCP 工具
    val context7Registry = createContext7McpRegistry()

    // 合併工具註冊表
    return basicTools + context7Registry
}

/**
 * 系統提示詞
 */
fun createSystemPrompt(): String {
    return """
        你是一個專業的 AI 開發助手，具備以下能力

        1. 回答 Koog AI 框架相關問題
        2. 使用 Context7 工具查詢最新的 API 文件和程式碼範例
        3. 提供準確、即時的技術支援

        當開發者詢問 Koog 相關的 API 用法時，請主動使用 Context7 工具查詢最新文件
        請確保提供的資訊是最新且正確的，而且使用正體中文回答
    """.trimIndent()
}

/**
 * 建立 Context7 MCP 工具註冊表
 */
suspend fun createContext7McpRegistry(): ToolRegistry {
    // 啟動 Context7 MCP 服務程序
    val context7Process = ProcessBuilder(
        "npx", "-y", "@upstash/context7-mcp"
    ).start()

    // 建立標準輸入輸出傳輸通道
    val transport = McpToolRegistryProvider.defaultStdioTransport(context7Process)

    // 從傳輸通道建立工具註冊表
    return McpToolRegistryProvider.fromTransport(transport)
}