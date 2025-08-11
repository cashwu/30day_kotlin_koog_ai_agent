package com.cashwu

import ai.koog.agents.core.agent.AIAgent

/**
 *
 * @author cash.wu
 * @since 2025/08/11
 *
 */
class BasicMultiLLMAssistant {

    private val multiLLMSetup = BasicMultiLLMSetup()

    fun createAgent(taskType: String): AIAgent<String, String> {
        val multiExecutor = multiLLMSetup.createBasicMultiExecutor()
        val model = multiLLMSetup.selectModelForTask(taskType)

        println("Task: $taskType, 🎯 模型：${model.provider} - ${model.id}")

        return AIAgent(
            executor = multiExecutor,
            systemPrompt = buildTaskPrompt(taskType),
            llmModel = model,
            maxIterations = 5
        )
    }

    private fun buildTaskPrompt(taskType: String): String {
        return when (taskType.lowercase()) {
            "chat", "conversation" -> """
                你是一個友善的 AI 助手，用正體中文回答問題
                - 以自然、溫暖的方式回應
                - 提供有用的資訊和建議
                - 保持禮貌和專業的態度
            """.trimIndent()

            "data", "analysis" -> """
                你是一個資料分析助手，用正體中文回答問題
                - 仔細分析提供的大量資訊
                - 提供結構化的分析結果
                - 善於處理複雜的資料和長文本
            """.trimIndent()

            "privacy", "local" -> """
                你是一個注重隱私的本地助手，用正體中文回答問題
                - 優先保護使用者隱私
                - 提供安全可靠的建議
                - 不會將資料傳送到外部服務
            """.trimIndent()

            else -> """
                你是一個通用 AI 助手，用正體中文回答問題
                - 根據使用者需求提供協助
                - 保持專業和有用的回應
                - 適時詢問更多細節以提供更好的服務
            """.trimIndent()
        }
    }
}