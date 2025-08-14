package com.cashwu

import ai.koog.prompt.cache.memory.InMemoryPromptCache
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.model.PromptExecutorExt.execute
import ai.koog.prompt.cache.model.PromptCache
import ai.koog.prompt.cache.redis.RedisPromptCache
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.cached.CachedPromptExecutor
import io.lettuce.core.RedisClient
import kotlin.time.Duration.Companion.days

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 建立快取
    val cache = InMemoryPromptCache(maxEntries = 100)

    // 執行器
    val executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!)

    // 包裝成快取執行器
    val cachedExecutor = CachedPromptExecutor(
        cache,
        executor
    )

    val prompt = prompt("memory") {
        system {
            text(
                """
                你是一個友善的 AI 助手
                請使用正體中文回答
            """.trimIndent()
            )
        }
        user {
            text("什麼是 Kotlin 協程？請簡單的說明")
        }
    }

    println("=== 第一次詢問（會呼叫 API）===")
    var response = cachedExecutor.execute(prompt, OpenAIModels.CostOptimized.GPT4_1Mini)
    println("$response")
    println("\nAI : ${response.content}")

    println("\n=== 第二次詢問相同問題（使用 memory 快取）===")
    response = cachedExecutor.execute(prompt, OpenAIModels.CostOptimized.GPT4_1Mini)
    println("\n$response")
    println("\nAI : ${response.content}")
}
