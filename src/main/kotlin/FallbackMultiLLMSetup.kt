package com.cashwu

import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.executor.model.PromptExecutorExt.execute
import io.lettuce.core.KillArgs.Builder.user

/**
 *
 * @author cash.wu
 * @since 2025/08/11
 *
 */
class FallbackMultiLLMSetup : BasicMultiLLMSetup() {
    // 建立主要和備用執行器
    private val primaryExecutor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!)
//    private val primaryExecutor = simpleOpenAIExecutor("error")
    private val fallbackExecutor = simpleGoogleAIExecutor(ApiKeyManager.googleApiKey!!)

    // 定義主要和備用模型
    private val primaryModel = OpenAIModels.CostOptimized.GPT4_1Mini
    private val fallbackModel = GoogleModels.Gemini2_5Flash

    // 簡單的備用機制 - 主要邏輯
    suspend fun executeWithFallback(prompt: Prompt): String {
        return try {
            println("🔄 嘗試使用 OpenAI 供應商...")
            val result = primaryExecutor.execute(prompt, primaryModel)
            println("✅ OpenAI 供應商回應成功")

            result.content
        } catch (e: Exception) {
            println("❌ OpenAI 供應商失敗：${e.message}")
            println("🔄 切換到 Google 備用供應商...")

            try {
                val fallbackResult = fallbackExecutor.execute(prompt, fallbackModel)
                println("✅ Google 供應商回應成功")

                fallbackResult.content
            } catch (fallbackError: Exception) {
                println("❌ Google 備用供應商也失敗：${fallbackError.message}")
                throw Exception("所有 LLM 供應商都無法使用。主要錯誤：${e.message}，備用錯誤：${fallbackError.message}")
            }
        }
    }

    // 建立簡化的對話介面
    fun createSimpleFallbackChat(): SimpleFallbackChat {
        return SimpleFallbackChat(this)
    }

    // 簡單的對話包裝器
    class SimpleFallbackChat(private val setup: FallbackMultiLLMSetup) {

        suspend fun chat(question: String): String {
            // 建立簡單的文字提示
            val prompt = prompt("fallback-chat") {
                system("你是一個通用 AI 助手，用正體中文回答問題")
                user(question)
            }

            return setup.executeWithFallback(prompt)
        }
    }
}