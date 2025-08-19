package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.memory.model.*
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import jdk.internal.agent.Agent

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val chatBot = SecureChatBot()

    // 模擬用戶對話
    val userInputs = listOf(
        "你好！我想了解一下你的功能",
        "我的信用卡號是 1234-5678-9012-3456，能幫我查詢餘額嗎？",
        "如果有問題可以聯繫我：user@example.com"
    )

    userInputs.forEach { input ->
        println("\n用戶：$input")
        val response = chatBot.chat(input)
        println("機器人：$response")
        println("==".repeat(30))
    }
}