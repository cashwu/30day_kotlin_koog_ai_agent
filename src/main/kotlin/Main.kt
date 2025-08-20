package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.tools
import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.embeddings.local.OllamaEmbeddingModels
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.rag.vector.EmbeddingBasedDocumentStorage
import ai.koog.rag.vector.InMemoryVectorStorage
import ai.koog.rag.vector.JVMTextDocumentEmbedder
import java.nio.file.Files
import java.nio.file.Path

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    // 初始化 RAG 系統
    val embedder = LLMEmbedder(OllamaClient(), OllamaEmbeddingModels.NOMIC_EMBED_TEXT)
    val documentEmbedder = JVMTextDocumentEmbedder(embedder)
    val documentStorage = EmbeddingBasedDocumentStorage(documentEmbedder, InMemoryVectorStorage())

    // 載入知識庫文件（實際應用中可能從資料庫或 API 載入）
    val knowledgeBaseFiles = listOf(
        "faq/AI/shipping.txt",
        "faq/AI/returns.txt",
        "faq/AI/warranty.txt",
        "faq/AI/payment.txt",
        "faq/AI/product-info.txt"
    )

    knowledgeBaseFiles.forEach { fileName ->
        val path = Path.of("./$fileName")
        if (Files.exists(path)) {
            documentStorage.store(path)
            println("已載入：$fileName")
        }
    }

    // 建立工具註冊器
    val toolRegistry = ToolRegistry {
        tools(CustomerServiceTools(documentStorage))
    }

    // 建立智慧客服 Agent
    val customerServiceAgent = AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        systemPrompt = """
            你是一位專業的客服代表，負責回答客戶問題

            工作原則：
            1. 使用 searchKnowledgeBase 工具搜尋相關資訊
            2. 基於搜尋結果提供準確、有用的回答
            3. 如果知識庫中沒有相關資訊，誠實告知並建議其他解決方案
            4. 保持友善、專業的語調
            5. 回答要簡潔明瞭，避免冗長
        """.trimIndent(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini,
        toolRegistry = toolRegistry
    )

    // 模擬客戶對話
    val customerQueries = listOf(
        "我的訂單什麼時候會到貨？",
        "如何申請退換貨？",
        "產品保固期是多久？",
        "支援哪些付款方式？"
    )

    customerQueries.forEach { query ->
        println("\n" + "=".repeat(50))
        println("客戶問題：$query")
        println("客服回覆：")

        try {
            val response = customerServiceAgent.run(query)
            println(response)
        } catch (e: Exception) {
            println("處理問題時發生錯誤：${e.message}")
        }
    }
}