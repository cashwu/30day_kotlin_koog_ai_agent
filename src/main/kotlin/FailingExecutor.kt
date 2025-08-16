package com.cashwu

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.model.LLMChoice
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import kotlinx.coroutines.flow.Flow

class FailingExecutor : PromptExecutor {
    override suspend fun execute(prompt: Prompt, model: LLModel, tools: List<ToolDescriptor>): List<Message.Response> {
        throw Exception("模擬主要執行器故障")
    }

    override suspend fun executeStreaming(prompt: Prompt, model: LLModel): Flow<String> {
        throw Exception("模擬主要執行器串流故障")
    }

    override suspend fun moderate(prompt: Prompt, model: LLModel): ModerationResult {
        throw Exception("模擬主要執行器審核故障")
    }

    override suspend fun executeMultipleChoices(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): List<LLMChoice> {
        throw Exception("模擬主要執行器多選項故障")
    }
}