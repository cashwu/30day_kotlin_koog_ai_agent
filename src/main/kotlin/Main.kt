package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.google.GoogleModels

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    println("🤖 多 LLM 助手系統啟動中...")

    // 顯示可用的供應商
    println("📋 可用的 LLM 供應商：")
    ApiKeyManager.getAvailableProviders().forEach { provider ->
        println("   ✅ $provider")
    }

    try {
        val setup = BasicMultiLLMAssistant()

        println("\n✅ 多 LLM 助手系統已就緒！")

        // 顯示可用的任務類型
        println("\n📋 可用的任務類型：")
        println("   1. chat - 日常對話")
        println("   2. data - 資料分析")
        println("   3. privacy - 隱私保護（本地處理）")

        // 使用者輸入任務類型
        print("\n請輸入任務類型（chat/data/privacy）：")
        val taskType = readlnOrNull()?.trim() ?: "chat"

        // 建立對應的 Agent
        val agent = setup.createAgent(taskType)

        // 使用者輸入問題
        print("請輸入您的問題：")
        val question = "你好，你現在正在使用哪個模型回答問題？ 請具體回答出那一個模型"

        println("\n👤 使用者：$question")
        println("🤖 AI 回答：")
        val response = agent.run(question)
        println(response)

    } catch (e: Exception) {
        println("❌ 系統啟動失敗：${e.message}")
        e.printStackTrace()
    }
}