package com.cashwu

import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.llm.OllamaModels

/**
 *
 * @author cash.wu
 * @since 2025/08/11
 *
 */
open class BasicMultiLLMSetup {
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

    // 簡單的供應商模型選擇
    fun selectModelForTask(taskType: String): LLModel {
        return when (taskType.lowercase()) {
            // 日常對話的最佳選擇
            "chat", "conversation" -> OpenAIModels.CostOptimized.GPT4_1Mini
            // 大 Context 資料處理（
            "data", "analysis" -> GoogleModels.Gemini2_5Flash
            // Ollama
            "privacy", "local" -> OllamaModels.Meta.LLAMA_3_2_3B
            // 通用任務的平衡選擇
            else -> OpenAIModels.CostOptimized.GPT4_1Mini
        }
    }
}