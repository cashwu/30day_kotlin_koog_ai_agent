package com.cashwu

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val aiHelper = SafeAIHelper()

    println("🤖 測試安全 AI 助手")
    println("=".repeat(30))

    val questions = listOf(
        "你好",
        "什麼是 Kotlin 協程？ 請簡單回答"
    )

    questions.forEach { question ->
        println("\n💬 問題：$question")
        val answer = aiHelper.askAI(question)
        println("🤖 回答：$answer")
        println("-".repeat(30))
    }
}
