package com.cashwu

import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.SocketTimeoutException

class RobustStreamingChat(private val executor: PromptExecutor) {

    fun streamWithRetry(
        prompt: Prompt,
        model: LLModel,
        maxRetries: Int = 3
    ): Flow<String> = flow {
        var attempt = 0
        var success = false

        while (attempt <= maxRetries && !success) {
            try {
                executor.executeStreaming(prompt, model).collect { token ->
                    emit(token)

                    // 模擬出錯的情況
                    delay(100L)
                    throw SocketTimeoutException("Dummy timeout")
                }
                success = true
            } catch (e: Exception) {
                attempt++
                if (attempt < maxRetries) {
                    emit("\n[連線中斷，正在重新嘗試... ($attempt/$maxRetries)]\n")
                    // 指數退避
                    delay(1000L * attempt)
                } else {
                    emit("\n[連線失敗，請稍後再試... ($attempt/$maxRetries)]\n")
                    throw e
                }
            }
        }
    }
}