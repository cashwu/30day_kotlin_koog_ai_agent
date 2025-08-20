package com.cashwu

import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 建立 OpenAI 用戶端
    val client = OpenAILLMClient(ApiKeyManager.openAIApiKey!!)

    // 建立 embedder，使用 TextEmbedding3Small 模型
    val embedder = LLMEmbedder(client, OpenAIModels.Embeddings.TextEmbedding3Small)

    val index = SimpleDocumentIndex(embedder)

    // 加入範例文件
    index.addDocument(
        "doc1",
        "人工智慧正在改變軟體開發的方式，自動化工具讓開發者更專注於創意和解決方案"
    )

    index.addDocument(
        "doc2",
        "Kotlin 是一個現代的程式語言，提供簡潔的語法和強大的類型安全性"
    )

    index.addDocument(
        "doc3",
        "機器學習模型訓練需要大量的資料和運算資源，雲端平台提供了彈性的解決方案"
    )

    index.addDocument(
        "doc4",
        "今天的天氣很好，適合外出散步和運動"
    )

    // 搜索相關文件
    val query = "AI 如何幫助程式設計師？"
    val results = index.searchSimilarDocuments(query)

    println("\n查詢：$query")
    println("最相關的文件：")

    results.forEach { (docId, similarity) ->
        val docContent = index.getDocument(docId)
        println("$docId (相似度: %.3f) - ${docContent?.take(50)}...".format(similarity))
    }
}