package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.context.AIAgentContextBase
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.environment.ReceivedToolResult
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

/**
 *
 * @author cash.wu
 * @since 2025/08/21
 *
 */
class CustomerServiceAgentWithHistoryCompression {
    // 檢查歷史記錄是否過長（超過 10 條訊息就壓縮）
    private suspend fun AIAgentContextBase.shouldCompressHistory(): Boolean {
        return llm.readSession { prompt.messages.size > 10 }
    }

    private val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = """
            你是一個專業的客服助手，負責回答客戶問題。
            請用正體中文回應客戶，保持友善和專業的態度。
        """.trimIndent(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        strategy = createStrategy()
    )

    private fun createStrategy() = strategy<String, String>("customer-service-with-compression") {

        // 定義主要處理節點
        val processRequest by nodeLLMRequest()
        val executeTool by nodeExecuteTool()
        val sendToolResult by nodeLLMSendToolResult()

        // 歷史記錄壓縮節點 - 使用 FromLastNMessages 策略
        val compressHistory by nodeLLMCompressHistory<ReceivedToolResult>(
            strategy = HistoryCompressionStrategy.FromLastNMessages(5)
        )

        // 診斷節點 - 展示壓縮前後的狀態
        val diagnosticNode by node<ReceivedToolResult, ReceivedToolResult>("diagnostic") { toolResult ->
            println("📊 === 歷史記錄壓縮觸發 ===")

            // 顯示壓縮將要發生
            val beforeMessages = llm.readSession { prompt.messages.size }
            println("🔍 壓縮前訊息數量: $beforeMessages 條")
            println("⚡ 即將觸發壓縮：保留最近 5 條訊息，將早期對話摘要化")

            toolResult
        }

        // 壓縮後檢查節點
        val postCompressionCheck by node<ReceivedToolResult, ReceivedToolResult>("post_compression") { toolResult ->
            val afterMessages = llm.readSession { prompt.messages.size }
            println("✅ 壓縮完成！目前訊息數量: $afterMessages 條")

            toolResult
        }

        // 建立執行流程
        edge(nodeStart forwardTo processRequest)

        // 如果是助理回應，直接結束
        edge(processRequest forwardTo nodeFinish onAssistantMessage { true })

        // 如果需要使用工具，執行工具
        edge(processRequest forwardTo executeTool onToolCall { true })

        // 執行工具後檢查是否需要壓縮歷史
        edge(executeTool forwardTo diagnosticNode onCondition { shouldCompressHistory() })
        edge(diagnosticNode forwardTo compressHistory)
        edge(compressHistory forwardTo postCompressionCheck)
        edge(postCompressionCheck forwardTo sendToolResult)

        // 如果不需要壓縮，直接發送工具結果
        edge(executeTool forwardTo sendToolResult onCondition { !shouldCompressHistory() })

        // 處理工具結果後的後續動作
        edge(sendToolResult forwardTo executeTool onToolCall { true })
        edge(sendToolResult forwardTo nodeFinish onAssistantMessage { true })
    }

    suspend fun handleCustomerQuery(query: String): String {
        return agent.run(query)
    }
}