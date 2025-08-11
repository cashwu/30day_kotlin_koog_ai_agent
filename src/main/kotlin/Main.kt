package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.google.GoogleModels

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    println("🤖 帶有 Fallback 機制的多 LLM 助手系統啟動中...")

    // 顯示可用的供應商
    println("📋 可用的 LLM 供應商：")
    ApiKeyManager.getAvailableProviders().forEach { provider ->
        println("   ✅ $provider")
    }

    try {
        val setup = FallbackMultiLLMSetup()

        println("\n✅ Fallback 多 LLM 助手系統已就緒！")
        println("🛡️  當主要供應商失敗時，系統會自動切換到備用供應商")

        // 建立簡化的 Fallback 對話
        val chat = setup.createSimpleFallbackChat()

        val question = "你好，你現在正在使用哪個模型回答問題？ 請具體回答出那一個模型"

        println("\n👤 使用者：$question")
        println("🤖 AI 回答：")

        val response = chat.chat(question)
        println(response)

    } catch (e: Exception) {
        println("❌ 系統完全失敗：${e.message}")
        e.printStackTrace()
    }
}