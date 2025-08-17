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
    val customerService = SmartCustomerServiceAgent()

    println("=== 智慧客服系統演示 ===\n")

    val testQueries = listOf(
        "你好，我想查詢 ORDER001 的訂單狀態",
        "我的商品有問題，沒有辦法開機，有沒有辦法換新的，我真的很生氣，可不可以請人跟我聯絡",
        "請問你們的營業時間是什麼時候？"
    )

    testQueries.forEachIndexed { index, query ->
        println("📞 客戶諮詢 ${index + 1}：$query")
        println("=".repeat(50))

        try {
            val response = customerService.handleCustomerQuery(query)
            println("\n🤖 客服回應：")
            println(response)
        } catch (e: Exception) {
            println("❌ 處理失敗：${e.message}")
        }

        println("\n" + "=".repeat(60) + "\n")
    }
}