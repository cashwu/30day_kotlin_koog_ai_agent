package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

class OrderProcessingAgent {
    private val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = "你是一個專業的訂單處理助手",
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        // 使用自訂策略圖
        strategy = createOrderProcessingStrategy()
    )

    private fun createOrderProcessingStrategy() = strategy<String, String>("order_processing") {

        // 節點一：驗證訂單資料
        val validateOrderNode by node<String, String>("validate_order") { orderData ->
            println("🔍 正在驗證訂單資料...")

            // 模擬訂單驗證邏輯
            val lines = orderData.split("\n")
            val hasCustomerInfo = lines.any { it.contains("客戶：") }
            val hasProductInfo = lines.any { it.contains("商品：") }
            val hasAmount = lines.any { it.contains("金額：") }

            when {
                !hasCustomerInfo -> "錯誤：缺少客戶資訊"
                !hasProductInfo -> "錯誤：缺少商品資訊"
                !hasAmount -> "錯誤：缺少金額資訊"
                else -> {
                    println("✅ 訂單驗證通過")
                    "驗證通過：$orderData"
                }
            }
        }

        // 節點二：計算價格
        val calculatePriceNode by node<String, String>("calculate_price") { validatedOrder ->
            println("💰 正在計算訂單價格...")

            // 從訂單中提取金額
            val amountLine = validatedOrder.split("\n")
                .find { it.contains("金額：") }

            val amount = amountLine?.substringAfter("金額：")?.trim()?.toDoubleOrNull() ?: 0.0
            val tax = amount * 0.05 // 5% 稅金
            val total = amount + tax

            val result = "$validatedOrder\n稅金：$tax\n總計：$total"
            println("✅ 價格計算完成，總計：$total")
            result
        }

        // 節點三：生成訂單確認
        val generateConfirmationNode by node<String, String>("generate_confirmation") { orderWithPrice ->
            println("📝 正在生成訂單確認...")

            val confirmation = """
                📋 訂單確認書
                ================
                $orderWithPrice
                ================
                狀態：已確認
                確認時間：${java.time.LocalDateTime.now()}
            """.trimIndent()

            println("✅ 訂單確認書生成完成")
            confirmation
        }

        // 節點四：錯誤處理
        val handleErrorNode by node<String, String>("handle_error") { errorMessage ->
            println("❌ 處理訂單錯誤")

            """
                ⚠️ 訂單處理失敗
                錯誤原因：$errorMessage
                請檢查訂單資料並重新提交
            """.trimIndent()
        }

        // 定義執行流程
        edge(nodeStart forwardTo validateOrderNode)

        // 驗證成功 -> 計算價格
        edge(validateOrderNode forwardTo calculatePriceNode onCondition { result ->
            result.startsWith("驗證通過")
        })

        // 驗證失敗 -> 錯誤處理
        edge(validateOrderNode forwardTo handleErrorNode onCondition { result ->
            result.startsWith("錯誤")
        })

        // 計算價格 -> 生成確認
        edge(calculatePriceNode forwardTo generateConfirmationNode)

        // 所有路徑最終都到達結束點
        edge(generateConfirmationNode forwardTo nodeFinish)
        edge(handleErrorNode forwardTo nodeFinish)
    }

    suspend fun processOrder(orderData: String): String {
        return agent.run(orderData)
    }
}