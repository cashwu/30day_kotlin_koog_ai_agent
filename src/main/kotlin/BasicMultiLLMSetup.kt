package com.cashwu

import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.prompt.llm.LLMProvider

/**
 *
 * @author cash.wu
 * @since 2025/08/11
 *
 */
class BasicMultiLLMSetup {
    fun createBasicMultiExecutor(): MultiLLMPromptExecutor {

        val executors = mutableMapOf<LLMProvider, LLMClient>()

        ApiKeyManager.openAIApiKey?.let { apiKey ->
            executors[LLMProvider.OpenAI] = OpenAILLMClient(apiKey)
            println("✅ OpenAI GPT 執行器已加入（大 Context 處理）")
        }

        ApiKeyManager.googleApiKey?.let { apiKey ->
            executors[LLMProvider.Google] = GoogleLLMClient(apiKey)
            println("✅ Google Gemini 執行器已加入（大 Context 處理）")
        }

        ApiKeyManager.ollamaBaseUrl?.let { baseUrl ->
            executors[LLMProvider.Ollama] = OllamaClient(baseUrl)
            println("✅ Ollama 執行器已加入（本地隱私保護）")
        }

        return MultiLLMPromptExecutor(executors)
    }
}