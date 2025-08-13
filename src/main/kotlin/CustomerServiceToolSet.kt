package com.cashwu

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

@LLMDescription("客服工具集，包含訂單查詢、營業時間查詢和常見問題搜尋")
class CustomerServiceToolSet : ToolSet {

    @Tool
    @LLMDescription("查詢訂單狀態和配送資訊")
    fun lookupOrder(
        @LLMDescription("訂單編號，格式如 ORD-20241201-001")
        orderId: String
    ): String {
        return try {
            // 模擬訂單查詢邏輯
            when {
                orderId.isBlank() -> "請提供有效的訂單編號"
                orderId.startsWith("ORD-") -> {
                    val randomStatus = listOf(
                        "已確認，預計 3-5 個工作天送達",
                        "已出貨，配送中，預計明天送達",
                        "已送達，感謝您的購買",
                        "處理中，我們正在準備您的商品"
                    ).random()
                    "訂單 $orderId 狀態：$randomStatus"
                }
                else -> "訂單編號格式不正確，請確認後重新輸入"
            }
        } catch (e: Exception) {
            "查詢訂單時發生錯誤：${e.message}"
        }
    }

    @Tool
    @LLMDescription("查詢指定門市或地區的營業時間")
    fun getBusinessHours(
        @LLMDescription("門市名稱或地區，如：台北、高雄、台中")
        location: String
    ): String {
        return try {
            val businessHours = mapOf(
                "台北" to "週一至週日 9:00-22:00",
                "台中" to "週一至週日 10:00-21:00",
                "高雄" to "週一至週日 9:30-21:30",
                "桃園" to "週一至週日 9:00-21:00",
                "台南" to "週一至週日 10:00-20:00"
            )

            val normalizedLocation = location.trim()
            businessHours[normalizedLocation]
                ?: "目前僅提供台北、台中、高雄、桃園、台南地區的營業時間查詢。一般門市營業時間為週一至週日 9:00-21:00"

        } catch (e: Exception) {
            "查詢營業時間時發生錯誤：${e.message}"
        }
    }

    @Tool
    @LLMDescription("搜尋常見問題的解答")
    fun searchFaq(
        @LLMDescription("問題關鍵字，如：退款、配送、會員")
        keyword: String
    ): String {
        return try {
            val faqDatabase = mapOf(
                "退款" to "退款政策：商品收到後 7 天內可申請退款，商品需保持原包裝。退款處理時間約 7-14 個工作天。",
                "配送" to "配送時間：一般商品 3-5 個工作天，急件可選擇隔日配送（需加收費用）。",
                "會員" to "會員權益：免費註冊即享 95 折優惠，消費滿額可累積點數兌換禮品。",
                "保固" to "保固服務：電子產品提供 1 年保固，非人為損壞免費維修。",
                "客服" to "客服時間：週一至週五 9:00-18:00，客服專線：0800-123-456"
            )

            val result = faqDatabase.entries.find {
                it.key.contains(keyword) || keyword.contains(it.key)
            }

            result?.value ?: "很抱歉，沒有找到相關的常見問題。您可以嘗試其他關鍵字，或直接聯絡客服人員協助。"

        } catch (e: Exception) {
            "搜尋常見問題時發生錯誤：${e.message}"
        }
    }
}