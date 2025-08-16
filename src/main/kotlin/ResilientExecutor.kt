package com.cashwu

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.dsl.Prompt
import ai.koog.prompt.executor.model.LLMChoice
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ResilientExecutor(
    private val primaryExecutor: PromptExecutor,
    private val primaryModel: LLModel,
    private val fallbackExecutor: PromptExecutor,
    private val fallbackModel: LLModel
) : PromptExecutor {

    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): List<Response> {

        return try {
            println("🎯 嘗試使用主要模型 ${primaryModel.id}...")
            // 首先嘗試使用主要執行器（例如 OpenAI）
            primaryExecutor.execute(prompt, primaryModel, tools)

        } catch (e: Exception) {
            println("⚠️ 主要模型失敗：${e.message}")
            println("🔄 切換到備用模型 ${fallbackModel.id}...")

            try {
                // 主要執行器失敗時，切換到備用執行器（例如 Gemini）
                fallbackExecutor.execute(prompt, fallbackModel, tools)

            } catch (fallbackException: Exception) {
                println("❌ 備用模型也失敗：${fallbackException.message}")

                // 如果備用模型也失敗，拋出更詳細的錯誤訊息
                throw Exception(
                    "所有模型都無法處理請求。主要錯誤：${e.message}，備用錯誤：${fallbackException.message}"
                )
            }
        }
    }

    override suspend fun executeStreaming(prompt: Prompt, model: LLModel): Flow<String> {
        return try {
            println("🎯 嘗試使用主要模型 ${primaryModel.id} 進行串流處理...")
            primaryExecutor.executeStreaming(prompt, primaryModel)
        } catch (e: Exception) {
            println("⚠️ 主要模型串流處理失敗：${e.message}")
            println("🔄 切換到備用模型 ${fallbackModel.id} 進行串流處理...")

            try {
                fallbackExecutor.executeStreaming(prompt, fallbackModel)
            } catch (fallbackException: Exception) {
                println("❌ 備用模型串流處理也失敗：${fallbackException.message}")
                // 返回錯誤訊息作為串流
                flowOf(
                    "所有模型都無法處理串流請求。主要錯誤：${e.message}，備用錯誤：${fallbackException.message}"
                )
            }
        }
    }

    override suspend fun moderate(prompt: Prompt, model: LLModel): ModerationResult {
        return try {
            println("🎯 嘗試使用主要模型 ${primaryModel.id} 進行內容審核...")
            primaryExecutor.moderate(prompt, primaryModel)
        } catch (e: Exception) {
            println("⚠️ 主要模型內容審核失敗：${e.message}")
            println("🔄 切換到備用模型 ${fallbackModel.id} 進行內容審核...")

            try {
                fallbackExecutor.moderate(prompt, fallbackModel)
            } catch (fallbackException: Exception) {
                println("❌ 備用模型內容審核也失敗：${fallbackException.message}")
                throw Exception(
                    "所有模型都無法處理內容審核請求。主要錯誤：${e.message}，備用錯誤：${fallbackException.message}"
                )
            }
        }
    }

    override suspend fun executeMultipleChoices(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): List<LLMChoice> {
        return try {
            println("🎯 嘗試使用主要模型 ${primaryModel.id} 進行多選項處理...")
            primaryExecutor.executeMultipleChoices(prompt, primaryModel, tools)
        } catch (e: Exception) {
            println("⚠️ 主要模型多選項處理失敗：${e.message}")
            println("🔄 切換到備用模型 ${fallbackModel.id} 進行多選項處理...")

            try {
                fallbackExecutor.executeMultipleChoices(prompt, fallbackModel, tools)
            } catch (fallbackException: Exception) {
                println("❌ 備用模型多選項處理也失敗：${fallbackException.message}")
                throw Exception(
                    "所有模型都無法處理多選項請求。主要錯誤：${e.message}，備用錯誤：${fallbackException.message}"
                )
            }
        }
    }
}