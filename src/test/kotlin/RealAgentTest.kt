import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class RealAgentTest {

    @Test
    fun `integration test - real AI response`() = runTest {
        // 設定真實的 API 金鑰（從環境變數獲取）
        val openAiKey = System.getenv("OPENAI_API_KEY") ?: return@runTest

        val executor = SingleLLMPromptExecutor(
            OpenAILLMClient(openAiKey)
        )

        val agent = AIAgent(
            executor = executor,
            systemPrompt = "你是一個友善的助手，請用正體中文回答",
            llmModel = OpenAIModels.CostOptimized.GPT4_1Nano,
            temperature = 0.1 // 使用較低溫度讓回應更穩定
        )

        // 測試真實場景
        val response = agent.run("請簡短說明什麼是 AI")

        // 驗證基本品質
        assertTrue(response.isNotEmpty())
        assertTrue(response.contains("AI") || response.contains("人工智慧"))
    }
}
