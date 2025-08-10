package com.cashwu

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.model.PromptExecutorExt.execute

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    // 建立執行器（負責與 OpenAI 溝通）
    val executor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY"))

    // 建立包含對話歷史的提示
    val conversationPrompt = prompt("kotlin-consultation") {
        system("""
            你是一個專業的 Kotlin 開發顧問，能夠：
            - 記住之前討論的內容
            - 根據上下文提供連貫的建議
            - 用正體中文進行專業且友善的對話
        """.trimIndent())

        // 第一輪對話
        user("我想學習 Kotlin，它適合初學者嗎？")
        assistant("Kotlin 確實很適合初學者！它的語法簡潔易懂，而且與 Java 完全相容。如果你有程式設計基礎會學得更快，但即使是完全的新手也能輕鬆上手。你之前有接觸過其他程式語言嗎？")

        // 第二輪對話 - AI 會記住前面的討論
        user("我有一點 Java 經驗，想用 Kotlin 開發 AI 應用")
    }

    val response = executor.execute(conversationPrompt, OpenAIModels.CostOptimized.GPT4_1Mini)

    // content 是文字內容
    println("顧問回應：${response.content}")
}