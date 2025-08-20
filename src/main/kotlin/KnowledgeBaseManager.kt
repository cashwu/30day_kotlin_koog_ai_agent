package com.cashwu

import ai.koog.rag.vector.EmbeddingBasedDocumentStorage
import java.nio.file.Files
import java.nio.file.Path


class KnowledgeBaseManager(
    private val documentStorage: EmbeddingBasedDocumentStorage<Path>
) {
    private val categoryFolders = mapOf(
        "shipping" to listOf(
            "shipping-policy.txt" to "運送政策和時間說明",
            "delivery-time.txt" to "各地區配送時間表"
        ),
        "returns" to listOf(
            "return-process.txt" to "退貨流程和注意事項",
            "refund-policy.txt" to "退款政策和處理時間"
        ),
        "products" to listOf(
            "product-specs.txt" to "產品規格和功能說明",
            "warranty-info.txt" to "保固條款和維修服務"
        )
    )

    suspend fun loadKnowledgeBase(basePath: String = "./knowledge-base"): LoadResult {
        var successCount = 0
        var failureCount = 0
        val failedFiles = mutableListOf<String>()

        categoryFolders.forEach { (category, files) ->
            files.forEach { (fileName, description) ->
                val path = Path.of("$basePath/$category/$fileName")
                try {
                    if (Files.exists(path)) {
                        documentStorage.store(path)
                        println("✓ 已載入 $category/$fileName - $description")
                        successCount++
                    } else {
                        println("⚠ 文件不存在：$path")
                        failedFiles.add("$category/$fileName")
                        failureCount++
                    }
                } catch (e: Exception) {
                    println("✗ 載入失敗：$category/$fileName - ${e.message}")
                    failedFiles.add("$category/$fileName")
                    failureCount++
                }
            }
        }

        return LoadResult(successCount, failureCount, failedFiles)
    }

    data class LoadResult(
        val successCount: Int,
        val failureCount: Int,
        val failedFiles: List<String>
    )
}