package com.cashwu

import ai.koog.embeddings.base.Embedder
import ai.koog.embeddings.base.Vector

/**
 *
 * @author cash.wu
 * @since 2025/08/20
 *
 */
class SimpleDocumentIndex(private val embedder: Embedder) {

    private val documents = mutableMapOf<String, String>()
    private val documentEmbeddings = mutableMapOf<String, Vector>()

    suspend fun addDocument(id: String, content: String) {
        documents[id] = content
        documentEmbeddings[id] = embedder.embed(content)
        println("已加入文件：$id")
    }

    fun getDocument(id: String): String? {
        return documents[id]
    }

    suspend fun searchSimilarDocuments(
        query: String,
        maxResults: Int = 3
    ): List<Pair<String, Double>> {
        val queryEmbedding = embedder.embed(query)

        val similarities = documentEmbeddings.map { (docId, docEmbedding) ->
            val similarity = 1.0 - embedder.diff(queryEmbedding, docEmbedding)
            docId to similarity
        }

        return similarities
            .sortedByDescending { it.second }
            .take(maxResults)
    }
}