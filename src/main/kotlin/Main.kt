package com.cashwu

import ai.koog.prompt.cache.memory.InMemoryPromptCache
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.model.PromptExecutorExt.execute
import ai.koog.prompt.cache.model.PromptCache
import ai.koog.prompt.dsl.prompt

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    // 建立記憶體快取，限制最大 10 筆記錄
    val cache = InMemoryPromptCache(maxEntries = 100)

    val executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!)

    val prompt = prompt("memory") {
        system {
            text("""
                你是一個友善的 AI 助手
                請使用正體中文回答
            """.trimIndent())
        }
        user {
            text("什麼是 Kotlin 協程？請簡單的說明")
        }
    }

    val promptRequest = PromptCache.Request.create(prompt, emptyList())

    println("=== 第一次詢問（會呼叫 API）===")
    var cachedResponse = cache.get(promptRequest)
    if (cachedResponse != null) {
        println("AI (使用快取回應): ${cachedResponse.first().content}")
    } else {
        val response = executor.execute(prompt, OpenAIModels.CostOptimized.GPT4_1Mini)

        // 將回應存入快取
        cache.put(promptRequest, listOf(response))

        println("AI : ${response.content}")
    }

    println("\n=== 第二次詢問相同問題（使用快取）===")
    cachedResponse = cache.get(promptRequest)
    if (cachedResponse != null) {
        println("AI (使用快取回應): ${cachedResponse.first().content}")
    } else {
        val response = executor.execute(prompt, OpenAIModels.CostOptimized.GPT4_1Mini)

        // 將回應存入快取
        cache.put(promptRequest, listOf(response))

        println("AI : ${response.content}")
    }
}
