package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.tools
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

class SmartCustomerService {

    // 對話歷史儲存 - 使用簡單的 MutableList
    private val conversationHistory = mutableListOf<ConversationRecord>()

    // 工具註冊 - 包含客服相關工具
    private val toolRegistry = ToolRegistry {
        tools(CustomerServiceToolSet())
    }

    // AI Agent - 整合執行器、工具和提示
    private val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = createCustomerServicePrompt(),
        toolRegistry = toolRegistry,
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        temperature = 0.7
    )

    init {
        println("🤖  AI 客服機器人啟動完成！")
        println("💭  對話歷史功能已啟用")
        println("🔧  工具系統已載入：訂單查詢、營業時間、FAQ 搜尋")
    }

    // 專業客服提示系統設計
    private fun createCustomerServicePrompt(): String {
        return """
            你是一個專業、友善的 AI 客服助手，名字叫「9527」

            你的服務原則：
            1. 用正體中文回答，語調親切專業
            2. 耐心解答客戶的各種問題
            3. 提供清楚、有用的資訊
            4. 記住之前的對話內容，提供連貫的服務
            5. 遇到複雜問題時，建議客戶聯絡真人客服

            你有以下工具可以使用：
            - lookupOrder：查詢訂單狀態，需要客戶提供訂單編號
            - getBusinessHours：查詢門市營業時間，可以指定地區
            - searchFaq：搜尋常見問題解答，根據關鍵字查找

            使用工具的時機：
            - 客戶詢問訂單狀態時，使用 lookupOrder 工具
            - 客戶詢問營業時間時，使用 getBusinessHours 工具
            - 客戶有一般性問題時，使用 searchFaq 工具搜尋相關解答

            請記住，你的目標是讓每位客戶都感受到溫暖的服務體驗
        """.trimIndent()
    }

    /**
     * 處理客戶問題 - 核心對話功能，支援對話歷史和工具系統
     */
    suspend fun chat(customerMessage: String): String {
        return try {
            // 建立包含對話歷史的完整輸入
            val fullInput = buildString {
                // 加入對話歷史作為上下文
                if (conversationHistory.isNotEmpty()) {
                    appendLine("=== 對話歷史 ===")
                    conversationHistory.takeLast(5).forEach { record -> // 只取最近 5 輪對話
                        appendLine("客戶：${record.userMessage}")
                        appendLine("9527：${record.assistantResponse}")
                    }
                    appendLine("=== 當前問題 ===")
                }
                append(customerMessage)
            }

            // 使用 AIAgent 處理客戶問題（自動包含工具使用）
            val assistantResponse = agent.run(fullInput)

            // 儲存到對話歷史
            conversationHistory.add(
                ConversationRecord(
                    userMessage = customerMessage,
                    assistantResponse = assistantResponse
                )
            )

            assistantResponse

        } catch (e: Exception) {
            // 健全的錯誤處理機制
            println("處理對話時發生錯誤：${e.message}")
            "很抱歉，我現在遇到一些技術問題。請稍後再試，或直接撥打客服專線。"
        }
    }

    /**
     * 取得對話歷史統計資訊
     */
    fun getConversationStats(): String {
        return "📊 目前已進行 ${conversationHistory.size} 輪對話"
    }

    /**
     * 清除對話歷史
     */
    fun clearHistory() {
        conversationHistory.clear()
        println("🗑️ 對話歷史已清除")
    }

    /**
     * 開始互動式對話 - 使用 while 迴圈實現連續對話
     */
    suspend fun startInteractiveChat() {
        println("🎉  歡迎使用 AI 客服系統")
        println("💬  您可以開始提問，輸入 'exit' 結束對話，輸入 'stats' 查看統計")
        println("=".repeat(50))

        while (true) {
            print("\n💬 您的問題：")
            val input = readlnOrNull()?.trim()

            when {
                input.isNullOrEmpty() -> continue

                input.lowercase() == "exit" -> {
                    println("👋 感謝使用 AI 客服系統，祝您有美好的一天！")
                    println(getConversationStats())
                    break
                }

                input.lowercase() == "stats" -> {
                    println(getConversationStats())
                    continue
                }

                input.lowercase() == "clear" -> {
                    clearHistory()
                    continue
                }

                else -> {
                    try {
                        print("🤖 9527 回應中...")
                        val response = chat(input)
                        print("\r🤖 9527：$response\n")
                        println("-".repeat(50))
                    } catch (e: Exception) {
                        println("⚠️ 系統錯誤：${e.message}")
                    }
                }
            }
        }
    }
}
