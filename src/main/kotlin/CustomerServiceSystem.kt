package com.cashwu

import ai.koog.prompt.structure.json.JsonSchemaGenerator
import ai.koog.prompt.structure.json.JsonStructuredData

/**
 *
 * @author cash.wu
 * @since 2025/08/20
 *
 */
class CustomerServiceSystem {
    // 建立範例資料幫助 AI 理解
    val exampleResponses = listOf(
        CustomerServiceResponse(
            responseType = ResponseType.ORDER_INQUIRY,
            content = "您的訂單 ORD-2025-001 目前正在處理中，預計 3-5 個工作天內出貨",
            suggestedActions = listOf("追蹤物流狀態", "聯繫客服確認詳細時程"),
            requiresHumanIntervention = false,
            relatedOrderId = "ORD-2025-001"
        ),
        CustomerServiceResponse(
            responseType = ResponseType.COMPLAINT,
            content = "很抱歉造成您的困擾，我們會立即處理您的退貨申請",
            suggestedActions = listOf("安排退貨流程", "聯繫品質管理部門", "提供補償方案"),
            requiresHumanIntervention = true,
            relatedOrderId = null
        )
    )

    // 產生結構化資料定義
    val customerServiceStructure = JsonStructuredData.createJsonStructure<CustomerServiceResponse>(
        schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
        examples = exampleResponses,
        schemaType = JsonStructuredData.JsonSchemaType.SIMPLE,
    )
}