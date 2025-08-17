package com.cashwu

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolArgs
import ai.koog.agents.core.tools.ToolDescriptor
import kotlinx.serialization.Serializable

class SmartCustomerServiceAgentTool {

    class QueryOrderTool : SimpleTool<QueryOrderTool.Args>() {
        @Serializable
        class Args(val orderId: String) : ToolArgs

        override val argsSerializer = Args.serializer()
        override val descriptor = ToolDescriptor("query_order", "查詢訂單狀態")

        override suspend fun doExecute(args: Args): String {
            // 模擬查詢訂單
            val orders = mapOf(
                "ORDER001" to "已出貨",
                "ORDER002" to "處理中",
                "ORDER003" to "已取消",
                "ORDER004" to "已到貨，保固中",
            )
            println("🔧 執行 query_order：查詢訂單 ${args.orderId}")
            return orders[args.orderId] ?: "訂單不存在"
        }
    }

    // 自訂工具：發送通知
    class SendNotificationTool : SimpleTool<SendNotificationTool.Args>() {
        @Serializable
        class Args(val message: String, val channel: String) : ToolArgs

        override val argsSerializer = Args.serializer()
        override val descriptor = ToolDescriptor("send_notification", "發送通知給客戶")

        override suspend fun doExecute(args: Args): String {
            println("📱 通過 ${args.channel} 發送通知：${args.message}")
            return "通知已發送"
        }
    }

    // 自訂工具：升級到人工客服
    class EscalateToHumanTool : SimpleTool<EscalateToHumanTool.Args>() {
        @Serializable
        class Args(val reason: String) : ToolArgs

        override val argsSerializer = Args.serializer()
        override val descriptor = ToolDescriptor("escalate_to_human", "轉接人工客服")

        override suspend fun doExecute(args: Args): String {
            println("🎧 轉接人工客服，原因：${args.reason}")
            return "已轉接至人工客服"
        }
    }
}