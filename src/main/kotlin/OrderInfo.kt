package com.cashwu

import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 * @author cash.wu
 * @since 2025/08/20
 *
 */
@kotlinx.serialization.Serializable
@SerialName("OrderInfo")
@LLMDescription("完整的訂單資訊")
data class OrderInfo(
    @property:LLMDescription("訂單編號")
    val orderId: String,
    @property:LLMDescription("客戶資訊")
    val customer: CustomerInfo,
    @property:LLMDescription("訂單商品清單")
    val items: List<OrderItem>,
    @property:LLMDescription("訂單狀態")
    val status: OrderStatus,
    @property:LLMDescription("總金額")
    val totalAmount: Double
)

// 客戶資訊
@kotlinx.serialization.Serializable
@SerialName("CustomerInfo")
@LLMDescription("客戶基本資料")
data class CustomerInfo(
    @property:LLMDescription("客戶姓名")
    val name: String,
    @property:LLMDescription("電子郵件地址")
    val email: String,
    @property:LLMDescription("聯絡電話")
    val phone: String
)

// 訂單商品
@kotlinx.serialization.Serializable
@SerialName("OrderItem")
@LLMDescription("單項商品資訊")
data class OrderItem(
    @property:LLMDescription("商品編號")
    val productId: String,
    @property:LLMDescription("商品名稱")
    val productName: String,
    @property:LLMDescription("購買數量")
    val quantity: Int,
    @property:LLMDescription("單價")
    val price: Double
)

// 訂單狀態枚舉
@Serializable
@SerialName("OrderStatus")
enum class OrderStatus {
    PENDING,    // 待處理
    CONFIRMED,  // 已確認
    SHIPPED,    // 已出貨
    DELIVERED,  // 已送達
    CANCELLED   // 已取消
}