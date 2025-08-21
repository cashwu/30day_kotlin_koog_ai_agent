package com.cashwu

/**
 *
 * @author cash.wu
 * @since 2025/08/21
 *
 */
class FixedSizeChunker(
    private val chunkSize: Int = 1000,
    private val overlapSize: Int = 200
) {
    fun chunk(text: String): List<TextChunk> {
        val chunks = mutableListOf<TextChunk>()
        val words = text.split(" ")

        var startIndex = 0
        var chunkId = 0

        while (startIndex < words.size) {
            val endIndex = minOf(startIndex + chunkSize, words.size)
            val chunkText = words.subList(startIndex, endIndex).joinToString(" ")

            chunks.add(
                TextChunk(
                    id = "chunk_${chunkId++}",
                    content = chunkText,
                    startIndex = startIndex,
                    endIndex = endIndex
                )
            )

            // 使用重疊避免語意斷裂
            startIndex += chunkSize - overlapSize
        }

        return chunks
    }
}

data class TextChunk(
    val id: String,
    val content: String,
    val startIndex: Int,
    val endIndex: Int
)