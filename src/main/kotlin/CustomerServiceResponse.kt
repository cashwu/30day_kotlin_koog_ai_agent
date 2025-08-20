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
@Serializable
@SerialName("CustomerServiceResponse")
@LLMDescription("客服助手的回應結構")
data class CustomerServiceResponse(
    @property:LLMDescription("回應類型")
    val responseType: ResponseType,
    @property:LLMDescription("回應內容")
    val content: String,
    @property:LLMDescription("建議的後續動作")
    val suggestedActions: List<String>,
    @property:LLMDescription("是否需要人工介入")
    val requiresHumanIntervention: Boolean = false,
    @property:LLMDescription("相關訂單編號（如果適用）")
    val relatedOrderId: String? = null
)

@Serializable
enum class ResponseType {
    ERROR,           // AI 錯誤
    INFORMATION,    // 資訊查詢
    COMPLAINT,      // 客訴處理
    ORDER_INQUIRY,  // 訂單查詢
    TECHNICAL_SUPPORT, // 技術支援
    GENERAL        // 一般詢問
}