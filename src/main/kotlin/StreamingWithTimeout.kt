package com.cashwu

import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

class StreamingWithTimeout(private val executor: PromptExecutor) {

    fun execute(
        prompt: Prompt,
        model: LLModel,
        timeoutSeconds: Long = 5
    ): Flow<String> = flow {
        try {
            withTimeout(timeoutSeconds * 1000) {
                executor.executeStreaming(prompt, model).collect { token ->

                    // 模擬超過時間
                    delay(6000)

                    emit(token)
                }
            }
        } catch (e: TimeoutCancellationException) {
            emit("\n[回應超時，請稍後再試]")
        } catch (e: IllegalStateException) {
            emit("\n[回應超時，請稍後再試]")
        }
    }
}