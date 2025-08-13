package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

class WeatherAgent {

    private val toolRegistry = ToolRegistry {
        tool(WeatherTool)  // 我們的天氣查詢工具
    }

    private val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = """
            你是一個友善的天氣助手

            🌤️ 當使用者詢問天氣時
            - 使用 get_weather 工具查詢指定城市的天氣
            - 提供清楚、有用的天氣資訊
            - 可以給出貼心的建議（如是否需要帶傘、外套等）

            請用正體中文回應，保持友善的語調。
        """.trimIndent(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        toolRegistry = toolRegistry,
    )

    suspend fun queryWeather(query: String): String {
        return try {
            agent.run(query)
        } catch (e: Exception) {
            "抱歉，無法查詢天氣資訊：${e.message}"
        }
    }
}