package com.cashwu

import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.model.PromptExecutorExt.execute
import ai.koog.prompt.cache.model.PromptCache
import ai.koog.prompt.cache.redis.RedisPromptCache
import ai.koog.prompt.dsl.prompt
import io.lettuce.core.RedisClient
import kotlin.time.Duration.Companion.days

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 建立 Redis 客戶端連線
    val client = RedisClient.create("redis://:cash1234@localhost:6379")

    // 建立 Redis 快取，設定前綴和 TTL
    val cache = RedisPromptCache(
        client,
        "ai-app-cache:",
        1.days  // 快取保存 7 天
    )

    val executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!)

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

    val promptRequest = PromptCache.Request.create(prompt, emptyList())

    try {
        println("=== 第一次詢問（會呼叫 API）===")
        var cachedResponse = cache.get(promptRequest)
        if (cachedResponse != null) {
            println("AI (從 Redis 快取載入): ${cachedResponse.first().content}")
        } else {
            val response = executor.execute(prompt, OpenAIModels.CostOptimized.GPT4_1Mini)

            // 將回應存入 Redis 快取
            cache.put(promptRequest, listOf(response))

            println("AI : ${response.content}")
            println("(回應已存入 Redis 快取)")
        }

        println("\n=== 第二次詢問相同問題（使用 Redis 快取）===")
        cachedResponse = cache.get(promptRequest)
        if (cachedResponse != null) {
            println("AI (從 Redis 快取載入): ${cachedResponse.first().content}")
        } else {
            val response = executor.execute(prompt, OpenAIModels.CostOptimized.GPT4_1Mini)

            // 將回應存入 Redis 快取
            cache.put(promptRequest, listOf(response))

            println("AI : ${response.content}")
            println("(回應已存入 Redis 快取)")
        }
    } finally {
        cache.close()
    }
}
