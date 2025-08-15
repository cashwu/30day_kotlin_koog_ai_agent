package com.cashwu

import kotlinx.coroutines.delay

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val greeter = PersonalizedGreeter()

    println("🤖 個人化問候助手啟動")
    println("=".repeat(50))

    // === 第一次互動：新使用者 ===
    println("\n👋 第一次見面")
    println("=".repeat(20))

    val firstResponse = greeter.greetUser(
        userInput = "你好"
    )

    println("使用者：你好")
    println("助手：${firstResponse.response}")
    println("📊 記憶體狀態：${if (firstResponse.hasMemory) "有記憶" else "無記憶"}")

    delay(1000)

    // === 自我介紹：儲存姓名 ===
    println("\n📝 自我介紹")
    println("=".repeat(20))

    val introResponse = greeter.greetUser(
        userInput = "我是 Cash"
    )

    println("使用者：我是 Cash")
    println("助手：${introResponse.response}")
    println("📊 記憶體狀態：${if (introResponse.hasMemory) "有記憶" else "無記憶"}")
    println("👤 記住的姓名：${introResponse.userName ?: "未記住"}")

    delay(1000)

    // === 第二次互動：展現記憶 ===
    println("\n🎯 個人化服務")
    println("=".repeat(20))

    val personalizedResponse = greeter.greetUser(
        userInput = "今天天氣如何？"
    )

    println("使用者：今天天氣如何？")
    println("助手：${personalizedResponse.response}")
    println("📊 記憶體狀態：${if (personalizedResponse.hasMemory) "有記憶" else "無記憶"}")
    println("👤 識別身份：${personalizedResponse.userName ?: "未識別"}")

    delay(1000)

    println("\n✨ 記憶體系統展示完成！")
}
