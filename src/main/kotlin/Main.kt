package com.cashwu

import ai.koog.agents.memory.model.*

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val sanitizer = DataSanitizer()

    // 測試案例
    val testInputs = listOf(
        "我的信用卡號是 1234-5678-9012-3456",
        "請聯繫我：john.doe@example.com",
        "我的卡號是 1234567890123456，信箱是 test@gmail.com",
        "今天天氣很好" // 正常文字
    )

    testInputs.forEach { input ->
        val result = sanitizer.sanitize(input)

        println("原始輸入：$input")
        println("脫敏結果：${result.sanitizedText}")
        if (result.hasSensitiveData) {
            println("檢測到：${result.detectedTypes.joinToString(", ")}")
        }
        println("---")
    }
}