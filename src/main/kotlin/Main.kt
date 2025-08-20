package com.cashwu

import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.embeddings.local.OllamaEmbeddingModels
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.rag.base.mostRelevantDocuments
import ai.koog.rag.vector.EmbeddingBasedDocumentStorage
import ai.koog.rag.vector.JVMFileVectorStorage
import ai.koog.rag.vector.JVMTextDocumentEmbedder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val embedder = LLMEmbedder(OllamaClient(), OllamaEmbeddingModels.NOMIC_EMBED_TEXT)
    val documentEmbedder = JVMTextDocumentEmbedder(embedder)
    val fileVectorStorage = JVMFileVectorStorage(root = Path.of("./vector-storage"))
    val documentStorage = EmbeddingBasedDocumentStorage(documentEmbedder, fileVectorStorage)

    val manager = KnowledgeBaseManager(documentStorage)
    val result = manager.loadKnowledgeBase()

    println("\n知識庫載入完成：")
    println("成功：${result.successCount} 個文件")
    if (result.failureCount > 0) {
        println("失敗：${result.failureCount} 個文件")
        println("失敗清單：${result.failedFiles.joinToString(", ")}")
    }
}

class BatchDocumentProcessor(
    private val documentStorage: EmbeddingBasedDocumentStorage<Path>,
    private val batchSize: Int = 50
) {
    suspend fun processBatch(documentPaths: List<Path>) {
        documentPaths.chunked(batchSize).forEach { batch ->
            withContext(Dispatchers.IO) {
                batch.forEach { path ->
                    try {
                        documentStorage.store(path)
                        println("✓ 已處理：${path.fileName}")
                    } catch (e: Exception) {
                        println("✗ 處理失敗：${path.fileName} - ${e.message}")
                    }
                }
            }
            println("已完成 ${batch.size} 個文件的處理")
        }
    }
}

class SearchCache {
    private val cache = mutableMapOf<String, Pair<List<Path>, Long>>()
    private val cacheTimeout = 300_000L // 5 分鐘

    fun getCachedResults(query: String): List<Path>? {
        val cached = cache[query] ?: return null
        return if (System.currentTimeMillis() - cached.second < cacheTimeout) {
            cached.first
        } else {
            cache.remove(query)
            null
        }
    }

    fun cacheResults(query: String, results: List<Path>) {
        cache[query] = Pair(results, System.currentTimeMillis())
    }
}

class SmartSearchManager(
    private val documentStorage: EmbeddingBasedDocumentStorage<Path>,
    private val cache: SearchCache = SearchCache()
) {
    suspend fun smartSearch(
        query: String,
        maxResults: Int = 5,
        useCache: Boolean = true
    ): List<Path> {
        // 先檢查快取
        if (useCache) {
            cache.getCachedResults(query)?.let { return it }
        }

        // 分階段搜尋策略
        val results = when {
            // 簡單關鍵字：使用寬鬆閾值
            query.split(" ").size <= 2 -> {
                documentStorage.mostRelevantDocuments(
                    query, maxResults, similarityThreshold = 0.6
                ).toList()
            }
            // 複雜問題：使用中等閾值
            query.length > 20 -> {
                documentStorage.mostRelevantDocuments(
                    query, maxResults, similarityThreshold = 0.7
                ).toList()
            }
            // 預設情況
            else -> {
                documentStorage.mostRelevantDocuments(
                    query, maxResults, similarityThreshold = 0.65
                ).toList()
            }
        }

        // 將結果存入快取
        if (useCache && results.isNotEmpty()) {
            cache.cacheResults(query, results)
        }

        return results
    }
}