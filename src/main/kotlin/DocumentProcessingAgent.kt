package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.snapshot.feature.Persistency
import ai.koog.agents.snapshot.providers.file.JVMFilePersistencyStorageProvider
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import java.nio.file.Path
import java.time.LocalDateTime

/**
 *
 * @author cash.wu
 * @since 2025/08/18
 *
 */
class DocumentProcessingAgent {
    private val agent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = "你是一個專業的文件處理助手",
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        strategy = createDocumentProcessingStrategy()
    ) {
        // 啟用自動檢查點功能
        install(Persistency) {
            storage = JVMFilePersistencyStorageProvider(
                root = Path.of("persistency/snapshots"),
                "document-processor",
            )
            enableAutomaticPersistency = true  // 每個節點執行後自動建立檢查點
        }
    }

    private fun createDocumentProcessingStrategy() = strategy<String, String>("document_processing") {

        // 節點一：收集資料
        val collectDataNode by node<String, String>("collect_data") { rawInput ->
            println("📊 開始收集文件資料...")

            // 模擬資料收集過程
            val processedData = llm.writeSession {
                updatePrompt {
                    user(
                        """
                        請模擬「資料收集」的過程，產生使用者要分析的相關資料 
                        
                        $rawInput
                        
                        產生的資料最後請加上
                        - 收集時間：${LocalDateTime.now()}
                        - 狀態：已收集
                    """.trimIndent()
                    )
                }
                requestLLM()
            }

            println("✅ 資料收集完成")
            processedData.content
        }

        // 節點二：分析資料
        val analyzeDataNode by node<String, String>("analyze_data") { collectedData ->
            println("🔍 開始分析文件內容...")

            // 使用 LLM 進行簡單分析
            val analysisResult = llm.writeSession {
                updatePrompt {
                    user(
                        """
                        請簡要分析以下文件內容的主要要點：
                        $collectedData
                    """.trimIndent()
                    )
                }
                requestLLM()
            }

            println("✅ 資料分析完成")
            "分析結果：\n${analysisResult.content}"
        }

        // 節點三：生成報告
        val generateReportNode by node<String, String>("generate_report") { analysisData ->
            println("📝 開始生成最終報告...")

            // 使用 LLM 生成結構化報告
            val report = llm.writeSession {
                updatePrompt {
                    user(
                        """
                        基於以下分析結果，請生成一份結構化的處理報告：
                        $analysisData

                        報告格式：
                        1. 摘要
                        2. 主要發現
                        3. 建議
                    """.trimIndent()
                    )
                }
                requestLLM()
            }

            println("✅ 報告生成完成")
            report.content
        }

        // 定義流程：收集 → 分析 → 報告
        edge(nodeStart forwardTo collectDataNode)
        edge(collectDataNode forwardTo analyzeDataNode)
        edge(analyzeDataNode forwardTo generateReportNode)
        edge(generateReportNode forwardTo nodeFinish)
    }

    suspend fun processDocument(input: String): String {
        return agent.run(input)
    }
}