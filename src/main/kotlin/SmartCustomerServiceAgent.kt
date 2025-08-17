package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

// 封裝客戶查詢和意圖的資料結構
data class CustomerQuery(
    val originalMessage: String,
    val intent: String
)

class SmartCustomerServiceAgent {

    private val toolRegistry = ToolRegistry {
        tool(SmartCustomerServiceAgentTool.QueryOrderTool())
        tool(SmartCustomerServiceAgentTool.SendNotificationTool())
        tool(SmartCustomerServiceAgentTool.EscalateToHumanTool())
    }

    private val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        toolRegistry = toolRegistry,
        systemPrompt = """
            你是一個專業的客服助手，能夠：
            1. 回答一般問題
            2. 查詢訂單狀態
            3. 處理客戶投訴
            4. 必要時轉接人工客服

            請使用正體中文回應客戶。
        """.trimIndent(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        strategy = createCustomerServiceStrategy()
    )

    private fun createCustomerServiceStrategy() = strategy<String, String>("customer_service") {

        // 節點：分析客戶意圖並保留原始訊息
        val analyzeIntentNode by node<String, CustomerQuery>("analyze_intent") { userMessage ->
            println("🔍 分析客戶意圖：$userMessage")

            // 簡單的意圖分析
            val intent = when {
                userMessage.contains("訂單") || userMessage.contains("ORDER") -> "order_query"
                userMessage.contains("投訴") || userMessage.contains("問題") || userMessage.contains("不滿") -> "complaint"
                userMessage.contains("退貨") || userMessage.contains("退款") -> "return_request"
                else -> "general_inquiry"
            }

            CustomerQuery(userMessage, intent)
        }

        // 節點：設定一般詢問提示詞
        val setupGeneralPromptNode by node<CustomerQuery, CustomerQuery>("setup_general_prompt") { query ->
            llm.writeSession {
                rewritePrompt {
                    prompt("setup_general_prompt") {
                        system("你是一個友善的客服助手，請專業地回答客戶的一般詢問。")
                        user(query.originalMessage)
                    }
                }
            }
            query // 傳遞 query 到下一個節點
        }

        // 節點：設定訂單查詢提示詞
        val setupOrderPromptNode by node<CustomerQuery, CustomerQuery>("setup_order_prompt") { query ->
            llm.writeSession {
                rewritePrompt {
                    prompt("setup_order_prompt") {
                        system(
                            """
                            你是一個客服助手，專門處理訂單查詢。
                            重要：如果客戶提到具體的訂單號碼（如 ORDER001、ORDER002 等），你必須使用 query_order 工具查詢訂單狀態，不要直接回答。
                            請先使用工具查詢，然後基於查詢結果回應客戶。
                        """.trimIndent()
                        )
                        user(query.originalMessage)
                    }
                }
            }
            query // 傳遞 query 到下一個節點
        }

        // 節點：設定投訴處理提示詞
        val setupComplaintPromptNode by node<CustomerQuery, CustomerQuery>("setup_complaint_prompt") { query ->
            llm.writeSession {
                rewritePrompt {
                    prompt("setup_complaint_prompt") {
                        system(
                            """
                            客戶有投訴需要處理。請：
                            1. 表達同理心和歉意
                            2. 詢問具體問題詳情
                            3. 提供解決方案
                            4. 重要：如果問題嚴重或複雜，你必須使用 escalate_to_human 工具轉接人工客服
                            5. 如果需要發送通知給客戶，使用 send_notification 工具
                            優先使用適當的工具來處理投訴。
                        """.trimIndent()
                        )
                        user(query.originalMessage)
                    }
                }
            }
            query // 傳遞 query 到下一個節點
        }

        // 節點：處理一般詢問（使用 nodeLLMRequest）
        val handleGeneralInquiryNode by nodeLLMRequest("handle_general", allowToolCalls = false)

        // 節點：處理訂單查詢（使用 nodeLLMRequest 支援工具）
        val handleOrderQueryNode by nodeLLMRequest("handle_order", allowToolCalls = true)

        // 節點：處理投訴（使用 nodeLLMRequest 支援工具）
        val handleComplaintNode by nodeLLMRequest("handle_complaint", allowToolCalls = true)

        // 節點：執行工具
        val executeToolNode by nodeExecuteTool("execute_tool")

        // 節點：發送工具結果給 LLM
        val sendToolResultNode by nodeLLMSendToolResult("send_tool_result")

        // 節點：生成最終回應（基於工具結果）
        val generateFinalResponseNode by node<String, String>("generate_final_response") { _ ->
            llm.writeSession {
                updatePrompt {
                    system(
                        """
                        現在請根據工具執行的結果，為客戶提供完整、專業且有幫助的回應。
                        請：
                        1. 直接回答客戶的問題
                        2. 基於工具查詢結果提供具體信息
                        3. 保持友善和專業的語調
                        4. 如果需要，詢問客戶是否還有其他需要協助的地方
                        
                        不要只是確認工具已執行，而要基於結果提供實質性的回應。
                    """.trimIndent()
                    )
                }
                requestLLMWithoutTools().content
            }
        }

        // 定義執行流程
        edge(nodeStart forwardTo analyzeIntentNode)

        // 根據意圖分流到提示詞設定節點
        edge(analyzeIntentNode forwardTo setupGeneralPromptNode onCondition { query ->
            query.intent == "general_inquiry"
        })

        edge(analyzeIntentNode forwardTo setupOrderPromptNode onCondition { query ->
            query.intent == "order_query"
        })

        edge(analyzeIntentNode forwardTo setupComplaintPromptNode onCondition { query ->
            query.intent == "complaint" || query.intent == "return_request"
        })

        // 從提示詞設定節點到 LLM 處理節點
        edge(setupGeneralPromptNode forwardTo handleGeneralInquiryNode transformed { _ -> "" })
        edge(setupOrderPromptNode forwardTo handleOrderQueryNode transformed { _ -> "" })
        edge(setupComplaintPromptNode forwardTo handleComplaintNode transformed { _ -> "" })

        // 一般詢問直接結束（不需要工具）
        edge(handleGeneralInquiryNode forwardTo nodeFinish onAssistantMessage { true })

        // 訂單查詢流程：優先工具執行，但有fallback
        edge(handleOrderQueryNode forwardTo executeToolNode onToolCall { true })
        edge(handleOrderQueryNode forwardTo nodeFinish onAssistantMessage { true })

        // 投訴處理流程：優先工具執行，但有fallback
        edge(handleComplaintNode forwardTo executeToolNode onToolCall { true })
        edge(handleComplaintNode forwardTo nodeFinish onAssistantMessage { true })

        // 工具執行流程
        edge(executeToolNode forwardTo sendToolResultNode)
        edge(sendToolResultNode forwardTo executeToolNode onToolCall { true })
        edge(sendToolResultNode forwardTo generateFinalResponseNode onAssistantMessage { true })

        // 最終回應後結束
        edge(generateFinalResponseNode forwardTo nodeFinish)
    }

    suspend fun handleCustomerQuery(query: String): String {
        return agent.run(query)
    }
}