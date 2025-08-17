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

    // 建立整合了 Playwright MCP 工具的 AI Agent
    val agent = createPlaywrightAgent(ApiKeyManager.openAIApiKey!!)

    val task = """
        打開瀏覽器，幫我到 https://blog.cashwu.com 網站，
        然後到「關於我」的相關頁面，
        然後給我作者相關的自我介紹
    """.trimIndent()

    try {
        println("\n正在執行任務... $task")
        val response = agent.run(task)
        println("\n回應: $response")
    } catch (e: Exception) {
        println("\n錯誤: ${e.message}")
    }
}


/**
 * 建立整合 Playwright 的 AI Agent
 */
suspend fun createPlaywrightAgent(apiKey: String): AIAgent<String, String> {
    return AIAgent(
        executor = simpleOpenAIExecutor(apiKey),
        strategy = singleRunStrategy(),
        systemPrompt = createPlaywrightSystemPrompt(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        toolRegistry = createPlaywrightToolRegistry()
    )
}

/**
 * 建立包含 Playwright MCP 工具的註冊表
 */
suspend fun createPlaywrightToolRegistry(): ToolRegistry {
    // 基礎工具
    val basicTools = ToolRegistry {
        tool(AskUser)
    }

    // Playwright MCP 工具
    val playwrightRegistry = createPlaywrightMcpRegistry()

    // 合併工具註冊表
    return basicTools + playwrightRegistry
}

/**
 * Playwright 系統提示詞
 */
fun createPlaywrightSystemPrompt(): String {
    return """
        你是一個專業的網頁自動化助手，具備以下能力

        1. 使用瀏覽器自動化工具執行網頁操作
        2. 瀏覽指定網站並與頁面元素互動
        3. 擷取網頁內容並分析結果
        4. 執行複雜的多步驟網頁操作流程

        當用戶要求執行網頁相關任務時，請使用 Playwright 工具進行自動化操作
        請確保操作步驟清晰且符合網站的使用條款，而且使用正體中文回答
    """.trimIndent()
}

/**
 * 建立 Playwright MCP 工具註冊表
 */
suspend fun createPlaywrightMcpRegistry(): ToolRegistry {
    // 啟動 Playwright MCP 服務程序
    val playwrightProcess = ProcessBuilder(
        "npx", "@playwright/mcp@latest", "--port", "8931"
    ).start()

    // 等待服務啟動
    Thread.sleep(3000)

    // 建立 SSE 傳輸通道
    val transport = McpToolRegistryProvider.defaultSseTransport("http://localhost:8931")

    // 從傳輸通道建立工具註冊表
    return McpToolRegistryProvider.fromTransport(transport)
}