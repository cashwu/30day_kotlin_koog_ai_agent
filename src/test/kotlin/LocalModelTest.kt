import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.prompt.llm.OllamaModels
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class LocalModelTest {

    @Test
    fun `local model test - basic response`() = runTest {
        // 假設已設定 Ollama 本地服務
        val executor = SingleLLMPromptExecutor(OllamaClient("http://localhost:11434"))

        val agent = AIAgent(
            executor = executor,
            systemPrompt = "你是一個簡潔的助手",
            llmModel = OllamaModels.Meta.LLAMA_3_2
        )

        val response = agent.run("說個笑話")
        assertTrue(response.isNotEmpty())
    }
}