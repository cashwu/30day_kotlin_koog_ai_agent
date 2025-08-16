package com.cashwu

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.model.LLMChoice
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AdvancedResilientExecutor(
    private val primaryExecutor: PromptExecutor,
    private val primaryModel: LLModel,
    private val fallbackExecutor: PromptExecutor,
    private val fallbackModel: LLModel,
    private val maxRetries: Int = 2,
    private val retryDelayMs: Long = 1000
) : PromptExecutor {

    override suspend fun execute(prompt: Prompt, model: LLModel, tools: List<ToolDescriptor>): List<Message.Response> {

        var attempt = 0
        var lastException: Exception? = null

        // 嘗試主要執行器
        while (attempt < maxRetries) {
            try {
                println("🎯 嘗試使用主要模型 ${primaryModel.id} (第 ${attempt + 1} 次)...")
                return primaryExecutor.execute(prompt, primaryModel, tools)

            } catch (e: Exception) {
                lastException = e
                attempt++

                println("⚠️ 主要模型第 $attempt 次嘗試失敗：${e.message}")

                if (attempt < maxRetries) {
                    println("⏳ 等待 ${retryDelayMs}ms 後重試...")
                    delay(retryDelayMs)
                }
            }
        }

        // 主要執行器多次重試都失敗，切換到備用執行器
        println("🔄 切換到備用模型 ${fallbackModel.id}...")

        attempt = 0
        while (attempt < maxRetries) {
            try {
                println("🎯 嘗試使用備用模型 ${fallbackModel.id} (第 ${attempt + 1} 次)...")
                return fallbackExecutor.execute(prompt, fallbackModel, tools)

            } catch (e: Exception) {
                lastException = e
                attempt++

                println("⚠️ 備用模型第 $attempt 次嘗試失敗：${e.message}")

                if (attempt < maxRetries) {
                    println("⏳ 等待 ${retryDelayMs}ms 後重試...")
                    delay(retryDelayMs)
                }
            }
        }

        // 所有嘗試都失敗了
        throw Exception(
            "經過 ${maxRetries * 2} 次嘗試後，所有模型都無法處理請求。最後錯誤：${lastException?.message}"
        )
    }

    override suspend fun executeStreaming(prompt: Prompt, model: LLModel): Flow<String> {
        return try {
            executeWithRetryFlow(
                primaryOperation = { executor, llmModel -> executor.executeStreaming(prompt, llmModel) },
                fallbackOperation = { executor, llmModel -> executor.executeStreaming(prompt, llmModel) }
            )
        } catch (e: Exception) {
            flowOf("容錯機制：${e.message}")
        }
    }

    override suspend fun moderate(prompt: Prompt, model: LLModel): ModerationResult {
        return try {
            executeWithRetry(
                primaryOperation = { executor, llmModel -> executor.moderate(prompt, llmModel) },
                fallbackOperation = { executor, llmModel -> executor.moderate(prompt, llmModel) }
            )
        } catch (e: Exception) {
            throw Exception("容錯機制：無法完成內容審核")
        }
    }

    override suspend fun executeMultipleChoices(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): List<LLMChoice> {
        return executeWithRetry(
            primaryOperation = { executor, llmModel -> executor.executeMultipleChoices(prompt, llmModel, tools) },
            fallbackOperation = { executor, llmModel -> executor.executeMultipleChoices(prompt, llmModel, tools) }
        )
    }

    private suspend fun <T> executeWithRetry(
        primaryOperation: suspend (PromptExecutor, LLModel) -> T,
        fallbackOperation: suspend (PromptExecutor, LLModel) -> T
    ): T {
        var attempt = 0
        var lastException: Exception? = null

        // 嘗試主要執行器
        while (attempt < maxRetries) {
            try {
                return primaryOperation(primaryExecutor, primaryModel)
            } catch (e: Exception) {
                lastException = e
                attempt++
                if (attempt < maxRetries) {
                    delay(retryDelayMs)
                }
            }
        }

        // 嘗試備用執行器
        attempt = 0
        while (attempt < maxRetries) {
            try {
                return fallbackOperation(fallbackExecutor, fallbackModel)
            } catch (e: Exception) {
                lastException = e
                attempt++
                if (attempt < maxRetries) {
                    delay(retryDelayMs)
                }
            }
        }

        throw Exception("所有重試都失敗：${lastException?.message}")
    }

    private suspend fun executeWithRetryFlow(
        primaryOperation: suspend (PromptExecutor, LLModel) -> Flow<String>,
        fallbackOperation: suspend (PromptExecutor, LLModel) -> Flow<String>
    ): Flow<String> {
        var attempt = 0
        var lastException: Exception? = null

        // 嘗試主要執行器
        while (attempt < maxRetries) {
            try {
                return primaryOperation(primaryExecutor, primaryModel)
            } catch (e: Exception) {
                lastException = e
                attempt++
                if (attempt < maxRetries) {
                    delay(retryDelayMs)
                }
            }
        }

        // 嘗試備用執行器
        attempt = 0
        while (attempt < maxRetries) {
            try {
                return fallbackOperation(fallbackExecutor, fallbackModel)
            } catch (e: Exception) {
                lastException = e
                attempt++
                if (attempt < maxRetries) {
                    delay(retryDelayMs)
                }
            }
        }

        throw Exception("所有串流重試都失敗：${lastException?.message}")
    }
}