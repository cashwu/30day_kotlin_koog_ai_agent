package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

/**
 *
 * @author cash.wu
 * @since 2025/08/18
 *
 */
class ResearchPlanExecuteAgent {

    private val toolRegistry = ToolRegistry {
        tool(ResearchPlanExecuteAgentTool.WebSearchTool())
        tool(ResearchPlanExecuteAgentTool.DataAnalysisTool())
        tool(ResearchPlanExecuteAgentTool.ProjectExecutionTool())
        tool(ResearchPlanExecuteAgentTool.QualityCheckTool())
    }

    private val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        toolRegistry = toolRegistry,
        systemPrompt = """
            你是一個專業的專案管理助手，能夠
            1. 進行市場和技術研究
            2. 制定詳細的執行計劃
            3. 協調專案執行
            4. 進行品質控制

            請使用正體中文回應
        """.trimIndent(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
//        llmModel = OpenAIModels.Chat.GPT4o,
        strategy = createResearchPlanExecuteStrategy(),
        maxIterations = 200
    )

    private fun createResearchPlanExecuteStrategy() = strategy<String, String>("research_plan_execute") {

        // 子圖一：研究階段
        val researchSubgraph by subgraph<String, String>(
            name = "research_phase",
            tools = listOf(
                ResearchPlanExecuteAgentTool.WebSearchTool(),
                ResearchPlanExecuteAgentTool.DataAnalysisTool()
            )
        ) {
            println("🔬 進入研究階段")

            // 初始化研究提示
            val initResearchNode by node<String, Unit>("init_research") { projectDescription ->
                llm.writeSession {
                    rewritePrompt {
                        prompt("research_prompt") {
                            system(
                                """
                                你現在進入研究階段。請針對專案進行全面研究：
                                1. 使用 web_search 搜尋相關市場資訊、技術趨勢、競爭分析
                                2. 使用 data_analysis 分析收集到的資料
                                3. 進行多輪研究，確保資訊完整
                                4. 最後整理研究結果並提供深入洞察

                                請積極使用工具進行研究，不要只是回答。
                            """.trimIndent()
                            )
                            user("專案描述：$projectDescription")
                        }
                    }
                }
            }

            val conductResearchNode by nodeLLMRequest("conduct_research")
            val executeResearchToolsNode by nodeExecuteTool("execute_research_tools")
            val processResearchResultsNode by nodeLLMSendToolResult("process_research_results")

            // 研究階段流程 - 確保能循環執行工具
            edge(nodeStart forwardTo initResearchNode)
            edge(initResearchNode forwardTo conductResearchNode transformed { "開始進行專案研究" })
            edge(conductResearchNode forwardTo executeResearchToolsNode onToolCall { true })
            edge(executeResearchToolsNode forwardTo processResearchResultsNode)

            // 允許多輪工具執行
            edge(processResearchResultsNode forwardTo executeResearchToolsNode onToolCall { true })

            // 只有當 LLM 明確表示研究完成時才結束
            edge(conductResearchNode forwardTo nodeFinish onAssistantMessage { true })
            edge(processResearchResultsNode forwardTo nodeFinish onAssistantMessage { true })
        }

        // 子圖二：規劃階段
        val planningSubgraph by subgraph<String, String>(
            name = "planning_phase",
            tools = listOf() // 規劃階段主要靠 LLM 思考，不需要外部工具
        ) {
            println("📋 進入規劃階段")

            val createPlanNode by node<String, String>("create_plan") { researchResults ->
                llm.writeSession {
                    rewritePrompt {
                        prompt("planning_prompt") {
                            system(
                                """
                                基於研究結果，請制定具體的執行任務清單。

                                重要：你必須輸出結構化的任務清單，格式如下：

                                任務1：[具體任務描述]｜優先級：[高/中/低]
                                任務2：[具體任務描述]｜優先級：[高/中/低]
                                ...

                                範例：
                                任務1：組建5人開發團隊並建立協作流程｜優先級：高
                                任務2：設計系統架構和選擇技術棧｜優先級：高
                                任務3：開發AI對話引擎核心模組｜優先級：高

                                請基於研究結果產生3-5個具體可執行的任務。
                            """.trimIndent()
                            )
                            user("研究結果：$researchResults")
                            user("請輸出結構化的任務清單")
                        }
                    }

                    // 直接請求 LLM 回應並返回內容
                    val response = requestLLMWithoutTools()
                    response.content
                }
            }

            // 簡化的規劃流程
            edge(nodeStart forwardTo createPlanNode)
            edge(createPlanNode forwardTo nodeFinish)
        }

        // 子圖三：執行階段
        val executionSubgraph by subgraph<String, String>(
            name = "execution_phase",
            tools = listOf(
                ResearchPlanExecuteAgentTool.ProjectExecutionTool(),
                ResearchPlanExecuteAgentTool.QualityCheckTool()
            )
        ) {
            println("⚡ 進入執行階段")

            val initExecutionNode by node<String, Unit>("init_execution") { executionPlan ->
                llm.writeSession {
                    rewritePrompt {
                        prompt("execution_prompt") {
                            system(
                                """
                                你現在是一個專案執行管理器。你的任務是解析任務清單並逐項執行。

                                重要規則：
                                1. 你必須使用 execute_task 工具來執行每一個任務
                                2. 每個任務執行完後，你必須使用 quality_check 工具進行品質檢查
                                3. 按優先級順序執行任務（高 → 中 → 低）
                                4. 執行完所有任務後，必須明確表示完成

                                執行流程：
                                解析任務清單 → 執行第一個任務 → 品質檢查 → 執行下一個任務 → ... → 完成所有任務

                                重要：當你執行完所有任務並進行品質檢查後，請回應文字訊息：
                                「所有任務執行完成，準備產生專案報告」
                                
                                注意：完成時不要再呼叫任何工具，只需發送上述完成訊息。
                            """.trimIndent()
                            )
                            user("執行計劃：$executionPlan")
                            user("請立即解析上述任務清單，並開始執行第一個高優先級的任務。請使用 execute_task 工具。")
                        }
                    }
                }
            }

            val executeTasksNode by nodeLLMRequest("execute_tasks")
            val executeToolsNode by nodeExecuteTool("execute_tools")
            val processExecutionResultsNode by nodeLLMSendToolResult("process_execution_results")

            val generateFinalReportNode by node<String, String>("generate_final_report") { _ ->
                llm.writeSession {
                    rewritePrompt {
                        prompt("final_report_prompt") {
                            system(
                                """
                                請根據上述執行過程和結果，產生最終的專案執行報告。
                                
                                報告格式：
                                # [專案名稱] - 專案執行報告
                                
                                ## 研究階段成果
                                [總結研究發現和洞察]
                                
                                ## 執行計劃與進度
                                [列出執行的任務和進度]
                                
                                ## 品質檢查結果
                                [總結品質檢查結果]
                                
                                ## 專案狀態總結
                                [整體進度和下一步建議]
                                
                                請產生一份完整、專業的專案執行報告。
                            """.trimIndent()
                            )
                            user("請產生專案執行報告")
                        }
                    }

                    val response = requestLLMWithoutTools()
                    response.content
                }
            }

            // 執行階段流程 - 恢復工具呼叫完整性
            edge(nodeStart forwardTo initExecutionNode)
            edge(initExecutionNode forwardTo executeTasksNode transformed { "立即解析任務清單並執行第一個任務" })
            edge(executeTasksNode forwardTo executeToolsNode onToolCall { true })
            edge(executeToolsNode forwardTo processExecutionResultsNode)

            // 允許多輪工具執行（保持工具呼叫完整性）
            edge(processExecutionResultsNode forwardTo executeToolsNode onToolCall { true })

            // 改進的完成檢測：當 LLM 回應文字而非工具呼叫時進入報告生成
            edge(executeTasksNode forwardTo generateFinalReportNode onAssistantMessage { response ->
                response.content.contains("所有任務執行完成") ||
                        response.content.contains("準備產生報告") ||
                        response.content.contains("任務全部完成") ||
                        response.content.contains("執行完畢")
            })

            edge(processExecutionResultsNode forwardTo generateFinalReportNode onAssistantMessage { response ->
                response.content.contains("所有任務執行完成") ||
                        response.content.contains("準備產生報告") ||
                        response.content.contains("任務全部完成") ||
                        response.content.contains("執行完畢")
            })

            // 最終報告輸出
            edge(generateFinalReportNode forwardTo nodeFinish)
        }

        // 主流程：串接三個子圖
        nodeStart then researchSubgraph then planningSubgraph then executionSubgraph then nodeFinish
    }

    suspend fun executeProject(projectDescription: String): String {
        return agent.run(projectDescription)
    }
}