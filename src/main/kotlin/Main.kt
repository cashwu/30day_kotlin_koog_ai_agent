package com.cashwu

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 建立客服助手 Agent
    val customerServiceAgent = CustomerServiceAgent()

    // 模擬客戶詢問
    val inquiries = listOf(
        "我的訂單 ORD-2025-001 什麼時候會到貨？",
        "產品有品質問題，我要退貨！",
        "如何更改我的會員資料？",
        "APP 一直當機，無法正常使用"
    )

    // 處理每個客戶詢問
    inquiries.forEach { inquiry ->
        println("\n客戶詢問：$inquiry")
        println("=".repeat(50))

        val response = customerServiceAgent.handleInquiry(inquiry)
        println(response)
        println()
    }
}