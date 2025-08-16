package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.sdk.trace.samplers.Sampler

class SimpleMonitoring {
    // 建立一個帶監控功能的 Agent
    private val monitoredAgent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = """
           你是一個 AI 助手，請用正體中文回答問題 
        """.trimIndent(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        toolRegistry = ToolRegistry {
            tool(SayToUser)
            tool(FakeWeatherTool)
        }
    ) {
        // 安裝 OpenTelemetry 監控功能
        install(OpenTelemetry) {
            // 設定服務資訊
            setServiceInfo("ai-agent-demo", "1.0.0")

            // 設定取樣速率
            setSampler(Sampler.traceIdRatioBased(0.5))

            // 開啟詳細模式，可以看到更多資訊
            setVerbose(true)

            // 新增自定的資源屬性
            addResourceAttributes(
                mapOf(
                    AttributeKey.stringKey("custom.attribute") to "custom-value",
                ),
            )
        }
    }

    suspend fun runWithMonitoring(query: String): String {
        println("🚀 開始執行查詢: $query")
        val result = monitoredAgent.run(query)
        println("✅ 查詢完成")
        return result
    }
}