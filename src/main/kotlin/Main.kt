package com.cashwu

import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.embeddings.local.OllamaEmbeddingModels
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.rag.base.mostRelevantDocuments
import ai.koog.rag.vector.EmbeddingBasedDocumentStorage
import ai.koog.rag.vector.InMemoryVectorStorage
import ai.koog.rag.vector.JVMTextDocumentEmbedder
import java.nio.file.Files
import java.nio.file.Path

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 1. 建立 Embedder 和文件儲存系統
    val embedder = LLMEmbedder(OllamaClient(), OllamaEmbeddingModels.NOMIC_EMBED_TEXT)
    val documentEmbedder = JVMTextDocumentEmbedder(embedder)
    val rankedDocumentStorage = EmbeddingBasedDocumentStorage(documentEmbedder, InMemoryVectorStorage())

    // 2. 儲存 FAQ 文件
    // 建立示範文件內容（實際應用中會從檔案系統讀取）
    val faqData = listOf(
        "./faq/shipping.txt" to "商品包裏出貨時間為 1-3 個工作天，超商取貨需要額外 1-2 天。",
        "./faq/returns.txt" to "商品可在收到後 7 天內退換貨，需保持原包裝完整。",
        "./faq/warranty.txt" to "電子產品提供一年保固，保固期內免費維修或更換。",
        "./faq/payment.txt" to "支援信用卡、ATM轉帳、超商付款等多種付款方式。",
        "./faq/account.txt" to "可透過官網註冊會員帳號，享受更多優惠和服務。"
    )

    // 建立目錄並存儲文件
    faqData.forEach { (filename, content) ->
        val path = Path.of(filename)
        Files.createDirectories(path.parent)
        Files.writeString(path, content)
        rankedDocumentStorage.store(path)
        println("已載入：$filename")
    }

    // 3. 搜尋相關文件
    val query = "我的包裏什麼時候會到？"
    val relevantDocs = rankedDocumentStorage.mostRelevantDocuments(
        query = query,
        count = 2,                    // 取前 2 個最相關的文件
        similarityThreshold = 0.5     // 相似度闾值（調整為更寬鬆的闾值）
    )

    // 4. 顯示搜尋結果
    println("用戶問題：$query")
    println("找到 ${relevantDocs.count()} 個相關文件：")

    relevantDocs.forEachIndexed { index, doc ->
        println("${index + 1}. 文件：${doc.fileName}")
        try {
            val content = Files.readString(doc)
            println("內容：$content")
        } catch (e: Exception) {
            println("無法讀取檔案：${e.message}")
        }
        println("-".repeat(50))
    }
}