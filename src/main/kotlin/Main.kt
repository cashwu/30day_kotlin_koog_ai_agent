package com.cashwu

import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.embeddings.local.OllamaEmbeddingModels
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.rag.vector.EmbeddingBasedDocumentStorage
import ai.koog.rag.vector.InMemoryVectorStorage
import ai.koog.rag.vector.JVMTextDocumentEmbedder

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val embedder = LLMEmbedder(OllamaClient(), OllamaEmbeddingModels.NOMIC_EMBED_TEXT)
    val documentEmbedder = JVMTextDocumentEmbedder(embedder)
    val documentStorage = EmbeddingBasedDocumentStorage(documentEmbedder, InMemoryVectorStorage())

    val manager = KnowledgeBaseManager(documentStorage)
    val result = manager.loadKnowledgeBase()

    println("\n知識庫載入完成：")
    println("成功：${result.successCount} 個文件")
    if (result.failureCount > 0) {
        println("失敗：${result.failureCount} 個文件")
        println("失敗清單：${result.failedFiles.joinToString(", ")}")
    }
}