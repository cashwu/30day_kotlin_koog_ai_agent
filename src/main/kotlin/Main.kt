package com.cashwu

import kotlinx.coroutines.delay

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    println("🎉  AI 客服機器人技術展示")
    println("=".repeat(35))

    // 建立客服機器人實例
    val chatbot = SmartCustomerService()

    // 模擬一些客戶對話 - 現在有上下文記憶和工具使用
    val conversations = listOf(
        "你好！我想了解你們的服務",
        "請問台北門市的營業時間？", // 測試營業時間查詢工具
        "我想查詢訂單 ORD-20241201-001 的狀態", // 測試訂單查詢工具
        "請問你們的退款政策是什麼？", // 測試 FAQ 搜尋工具
        "我剛才查詢的訂單如果要退款該怎麼辦？", // 測試記憶功能 + FAQ
        "謝謝你的協助！"
    )

    // 逐一進行對話
    conversations.forEachIndexed { index, message ->
        println("\n💬 客戶：$message")

        val response = chatbot.chat(message)
        println("🤖 9527：$response")

        // 顯示對話統計
        if (index == conversations.size - 1) {
            println("\n${chatbot.getConversationStats()}")
        }

        // 模擬對話間隔
        delay(1000)
    }

    println("\n🎊 對話測試完成！")
}
