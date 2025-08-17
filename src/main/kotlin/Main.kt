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

    val processor = OrderProcessingAgent()

    println("=== 訂單處理策略圖演示 ===\n")

    // 測試正常訂單
    val validOrder = """
        客戶：張小明
        商品：筆記型電腦
        金額：50000
    """.trimIndent()

    println("📝 處理正常訂單：")
    try {
        val result = processor.processOrder(validOrder)
        println("\n🎯 處理結果：")
        println(result)
    } catch (e: Exception) {
        println("❌ 處理失敗：${e.message}")
    }

    println("\n" + "=".repeat(50) + "\n")

    // 測試異常訂單
    val invalidOrder = """
        客戶：李小華
        商品：智慧型手機
        // 缺少金額資訊
    """.trimIndent()

    println("📝 處理異常訂單：")
    try {
        val result = processor.processOrder(invalidOrder)
        println("\n🎯 處理結果：")
        println(result)
    } catch (e: Exception) {
        println("❌ 處理失敗：${e.message}")
    }
}