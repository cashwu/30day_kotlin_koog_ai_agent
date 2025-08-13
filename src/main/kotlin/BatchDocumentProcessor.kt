package com.cashwu

import kotlinx.coroutines.delay
import java.io.File

/**
 * 批量文件處理器
 * 提供批量處理多個文件的功能，支援批量摘要產生和錯誤處理
 */
class BatchDocumentProcessor(private val summarizer: DocumentSummarizer) {

    /**
     * 批量產生文件摘要
     * 依序處理多個文件並產生摘要，包含錯誤處理和 API 頻率限制控制
     * @param filePaths 要處理的文件路徑列表
     * @return 以檔案名稱為鍵、摘要內容為值的對應表，處理失敗的文件會包含錯誤訊息
     */
    suspend fun batchSummarize(filePaths: List<String>): Map<String, String> {
        val results = mutableMapOf<String, String>()

        filePaths.forEach { filePath ->
            try {
                val fileName = File(filePath).name
                println("正在處理：$fileName")

                val summary = summarizer.summarizeDocument(filePath)
                results[fileName] = summary

                println("✓ $fileName 處理完成")

                // 避免 API 頻率限制
                delay(3000)

            } catch (e: Exception) {
                println("✗ 處理 $filePath 時發生錯誤：${e.message}")
                results[filePath] = "處理失敗：${e.message}"
            }
        }

        return results
    }
}