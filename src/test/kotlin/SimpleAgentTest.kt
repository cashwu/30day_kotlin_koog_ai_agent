import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.*
import ai.koog.agents.testing.feature.withTesting
import ai.koog.agents.testing.tools.getMockExecutor
import ai.koog.agents.testing.tools.mockLLMAnswer
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class SimpleAgentTest {
    @Test
    fun `should respond to greeting`() = runTest {
        // 建立 Mock 執行器
        val mockExecutor = getMockExecutor {
            // 當收到包含「你好」的訊息時，回應特定內容
            mockLLMAnswer("您好！我是客服助手，有什麼可以幫助您的嗎？") onRequestContains "你好"

            // 設定預設回應（當沒有符合條件時）
            mockLLMAnswer("我需要更多資訊才能幫助您").asDefaultResponse
        }

        // 建立測試用 Agent
        val agent = AIAgent(
            executor = mockExecutor,
            systemPrompt = "你是一個友善的客服助手",
            llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
        ) {
            withTesting() // 啟用測試模式
        }

        // 執行測試
        val result = agent.run("你好")

        // 驗證結果
        assertEquals("您好！我是客服助手，有什麼可以幫助您的嗎？", result)
    }

    object WeatherTool : SimpleTool<WeatherTool.Args>() {

        @Serializable
        data class Args(val text: String) : ToolArgs

        override val argsSerializer = Args.serializer()

        override suspend fun doExecute(args: Args): String {
            return ""
        }

        override val descriptor = ToolDescriptor(
            name = "get_weather",
            description = "查詢指定城市的天氣資訊",
            requiredParameters = listOf(
                ToolParameterDescriptor(
                    name = "city",
                    description = "城市名稱（支援中文或英文）",
                    type = ToolParameterType.String
                )
            )
        )
    }

    @Test
    fun `should use weather tool correctly`() = runTest {
        // 建立工具註冊表
        val toolRegistry = ToolRegistry {
            tool(WeatherTool)
        }

        val mockExecutor = getMockExecutor(toolRegistry) {
            // 模擬 LLM 決定呼叫天氣工具
            mockLLMToolCall(
                WeatherTool,
                WeatherTool.Args("台北")
            ) onRequestContains "台北天氣"

            // 模擬工具回傳結果
            mockTool(WeatherTool) alwaysReturns "台北今日晴天，溫度 25 度"

            // 設定 AI 回應
            mockLLMAnswer("台北今日晴天，溫度 25 度") onRequestContains "台北天氣"
        }

        val agent = AIAgent(
            executor = mockExecutor,
            systemPrompt = "你是天氣助手，可以查詢天氣資訊",
            toolRegistry = toolRegistry,
            llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
        ) {
            withTesting()
        }

        val result = agent.run("請告訴我台北天氣如何")
        assertTrue(result.contains("25 度") || result.contains("晴天"))
    }

    @Test
    fun `should execute basic workflow`() = runTest {
        val mockExecutor = getMockExecutor {
            // 設定多步驟回應
            mockLLMAnswer("我需要先了解您的問題") onRequestContains "問題"
            mockLLMAnswer("讓我為您提供解決方案") onRequestContains "了解"
            mockLLMAnswer("問題已解決").asDefaultResponse
        }

        val agent = AIAgent(
            executor = mockExecutor,
            systemPrompt = "你是問題解決助手",
            llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
        ) {
            withTesting()
        }

        // 測試基本執行
        var result = agent.run("我有一個問題需要協助")
        assertEquals("我需要先了解您的問題", result)

        result = agent.run("我想要了解相關的方案")
        assertEquals("讓我為您提供解決方案", result)

        result = agent.run("謝謝你的回答")
        assertEquals("問題已解決", result)
    }
}