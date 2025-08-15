package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.features.eventHandler.feature.EventHandler
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * @author cash.wu
 * @since 2025/08/15
 *
 */
class EventCustomerServiceBot {

    fun createBot(): AIAgent<String, String> {
        return AIAgent(
            executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
            systemPrompt = """
                你是一個友善的客服助手，專門協助客戶解決問題
                請用親切的語氣回應，並盡量提供有用的資訊
                使用正體中文回應 
            """.trimIndent(),
            llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
        ) {

            // 安裝事件處理功能
            install(EventHandler) {

                // 當客服機器人開始工作時
                onBeforeAgentStarted { eventContext ->
                    val currentTime = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

                    println("=".repeat(50))
                    println("🤖 客服機器人已啟動")
                    println("⏰ 開始時間：$currentTime")
                    println("🤖 Agent ID：${eventContext.agent.id}")
                    println("🎯 使用策略：${eventContext.strategy.name}")
                    println("🆔 執行 ID：${eventContext.runId}")
                    println("=".repeat(50))
                }

                // 當開始使用工具時（比如查詢資料庫、搜尋資訊等）
                onToolCall { eventContext ->
                    println("🔧 工具開始執行：")
                    println("   工具名稱：${eventContext.tool.name}")
                    println("   輸入參數：${eventContext.toolArgs}")
                    println("   執行 ID：${eventContext.runId}")
                    println("-".repeat(30))
                }

                // 當工具執行完成並有結果時
                onToolCallResult { eventContext ->
                    println("✅ 工具執行完成：")
                    println("   工具名稱：${eventContext.tool.name}")

                    // 顯示工具執行的簡要結果
                    val resultPreview = when {
                        eventContext.result.toString().length <= 100 -> eventContext.result.toString()
                        else -> "${eventContext.result.toString().take(97)}..."
                    }
                    println("   執行結果：$resultPreview")
                    println("-".repeat(30))
                }

                // 當 Agent 執行完成時
                onAgentFinished { eventContext ->
                    println("🎉 客服機器人執行完成")
                    println("   Agent ID：${eventContext.agentId}")
                    println("   執行 ID：${eventContext.runId}")
                    println("=".repeat(50))
                }

                // 當發生錯誤時
                onAgentRunError { eventContext ->
                    println("⚠️  客服系統發生問題：")
                    println("   錯誤訊息：${eventContext.throwable.message}")
                    println("   Agent ID：${eventContext.agentId}")
                    println("   發生時間：${LocalDateTime.now()}")

                    // 根據錯誤類型提供不同的處理建議
                    when {
                        eventContext.throwable.message?.contains("timeout") == true ->
                            println("   建議：網路連線可能不穩定，請稍後再試")
                        eventContext.throwable.message?.contains("quota") == true ->
                            println("   建議：API 配額可能已用完，請檢查帳戶狀態")
                        else ->
                            println("   建議：請檢查設定或聯繫技術支援")
                    }
                    println("=".repeat(50))
                }
            }
        }
    }
}