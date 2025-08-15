package com.cashwu

import kotlinx.coroutines.delay

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 建立客服機器人
    val bot = EventCustomerServiceBot().createBot()

    // 模擬客戶對話
    val customerQuestions = listOf(
        "你好，請問你們的營業時間是什麼時候？",
        "運費是怎麼計算的？"
    )

    customerQuestions.forEach { question ->
        println("👤 客戶問題：$question")

        try {
            val response = bot.run(question)
            println("🤖 客服回應：$response")
        } catch (e: Exception) {
            println("💥 處理失敗：${e.message}")
        }

        println("\n" + "=".repeat(60) + "\n")
    }
}
