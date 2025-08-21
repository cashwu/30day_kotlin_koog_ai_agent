package com.cashwu

import ai.koog.embeddings.base.Embedder
import ai.koog.embeddings.base.Vector

/**
 *
 * @author cash.wu
 * @since 2025/08/21
 *
 */
class ChunkedDocumentIndex(
    private val embedder: Embedder,
    private val chunker: FixedSizeChunker = FixedSizeChunker()
) {
    private val chunks = mutableMapOf<String, TextChunk>()
    private val chunkEmbeddings = mutableMapOf<String, Vector>()

    suspend fun addDocument(docId: String, content: String) {
        val documentChunks = chunker.chunk(content)

        documentChunks.forEach { chunk ->
            val chunkKey = "${docId}_${chunk.id}"
            chunks[chunkKey] = chunk
            chunkEmbeddings[chunkKey] = embedder.embed(chunk.content)
            println("已處理分塊：$chunkKey (${chunk.content.take(50)}...)")
        }
    }

    suspend fun searchSimilarChunks(
        query: String,
        maxResults: Int = 5
    ): List<Pair<TextChunk, Double>> {
        val queryEmbedding = embedder.embed(query)

        val similarities = chunkEmbeddings.map { (chunkKey, chunkEmbedding) ->
            val similarity = 1.0 - embedder.diff(queryEmbedding, chunkEmbedding)
            chunks[chunkKey]!! to similarity
        }

        return similarities
            .sortedByDescending { it.second }
            .take(maxResults)
    }
}
