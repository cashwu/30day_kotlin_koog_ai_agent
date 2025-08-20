package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.message.Message

/**
 *
 * @author cash.wu
 * @since 2025/08/20
 *
 */
class CustomerServiceAgent() {

    // 建立客服策略
    private fun createStrategy() = strategy("customer-service") {
        val setup by nodeLLMRequest()

        val analyzeInquiry by node<Message.Response, CustomerServiceResponse> { _ ->
            val structureResponse = llm.writeSession {
                requestLLMStructured(
                    structure = CustomerServiceSystem().customerServiceStructure,
                    fixingModel = OpenAIModels.CostOptimized.GPT4oMini
                )
            }

            structureResponse.fold(
                onSuccess = {
                    it.structure
                },
                onFailure = {
                    // 提供預設的客服回應
                    CustomerServiceResponse(
                        responseType = ResponseType.ERROR,
                        content = "處理時發生錯誤，請稍後再試",
                        suggestedActions = listOf("重新提交請求", "聯繫人工客服"),
                        requiresHumanIntervention = true,
                        relatedOrderId = null
                    )
                }
            )
        }

        val processResponse by node<CustomerServiceResponse, String> { response ->
            val result = StringBuilder()
            result.appendLine("=== 客服助手回應 ===")
            result.appendLine("類型：${response.responseType}")
            result.appendLine("內容：${response.content}")

            if (response.suggestedActions.isNotEmpty()) {
                result.appendLine("\n建議後續動作：")
                response.suggestedActions.forEach { action ->
                    result.appendLine("• $action")
                }
            }

            if (response.requiresHumanIntervention) {
                result.appendLine("\n⚠️  此案件需要人工介入處理")
            }

            response.relatedOrderId?.let { orderId ->
                result.appendLine("\n相關訂單：$orderId")
            }

            result.toString()
        }

        edge(nodeStart forwardTo setup)
        edge(setup forwardTo analyzeInquiry)
        edge(analyzeInquiry forwardTo processResponse)
        edge(processResponse forwardTo nodeFinish)
    }

    // 執行客服查詢
    suspend fun handleInquiry(inquiry: String): String {

        // 初始化 PromptExecutor
        val promptExecutor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!)

        val agent = AIAgent(
            promptExecutor = promptExecutor,
            toolRegistry = ToolRegistry.EMPTY,
            strategy = createStrategy(),
            agentConfig = AIAgentConfig(
                prompt = prompt("customer-service-prompt") {
                    system(
                        """
                        您是一個專業的客服助手，負責處理各種客戶詢問。
                        請根據客戶的問題分析類型，提供適當的回應，並建議後續處理動作。

                        處理原則：
                        1. 保持友善和專業的態度
                        2. 準確判斷問題類型
                        3. 提供清楚的解決方案或資訊
                        4. 必要時建議轉接人工客服
                        """.trimIndent()
                    )
                },
                model = OpenAIModels.CostOptimized.GPT4oMini,
                maxAgentIterations = 5
            )
        )

        return agent.run(inquiry)
    }
}