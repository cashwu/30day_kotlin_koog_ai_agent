package com.cashwu

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.structure.executeStructured
import ai.koog.prompt.structure.json.JsonSchemaGenerator
import ai.koog.prompt.structure.json.JsonStructuredData

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val promptExecutor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!)

    // 建立範例資料幫助 AI 理解
    val exampleOrders = listOf(
        OrderInfo(
            orderId = "ORD-2025-001",
            customer = CustomerInfo(
                name = "王小明",
                email = "ming@example.com",
                phone = "0912-345-678"
            ),
            items = listOf(
                OrderItem(
                    productId = "PROD-001",
                    productName = "智慧手錶",
                    quantity = 1,
                    price = 8999.0
                )
            ),
            status = OrderStatus.PENDING,
            totalAmount = 8999.0
        )
    )

    // 產生結構化資料定義
    val orderStructure = JsonStructuredData.createJsonStructure<OrderInfo>(
        schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
        examples = exampleOrders,
        schemaType = JsonStructuredData.JsonSchemaType.SIMPLE,
    )

    val orderContent = "幫我建立一筆購買 iPhone 17 Air 的訂單，客戶是 9527，電話 0912-345-678"

    // 執行結構化請求
    val structuredResponse = promptExecutor.executeStructured(
        prompt = prompt("order-creation") {
            system(
                """
                您是一個專業的訂單處理助手。
                根據使用者的要求建立訂單資訊，確保所有必要欄位都完整填寫。
                """.trimIndent()
            )
            user(orderContent)
        },
        mainModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        structure = orderStructure,
        retries = 5,
    )

    println("訂單描述 - $orderContent")

    structuredResponse.getOrNull()?.let { order ->

        println("\nAI 回應內容")
        println("*".repeat(30))


        // 直接取得類型安全的 OrderInfo 物件
        println("\n完整內容：${order.structure}")

        println("\n訂單編號：${order.structure.orderId}")
        println("\n客戶姓名：${order.structure.customer.name}")
        println("\n總金額：$${order.structure.totalAmount}")
    }
}