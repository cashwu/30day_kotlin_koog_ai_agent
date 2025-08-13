package com.cashwu

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import kotlinx.io.files.Path

class DocumentSummarizer(private val client: GoogleLLMClient) {

    /**
     * 根據文件副檔名取得對應的 MIME 類型
     * @param filePath 文件路徑
     * @return 對應的 MIME 類型字串
     */
    private fun getMimeType(filePath: String): String {
        val extension = kotlin.io.path.Path(filePath).fileName.toString().lowercase().substringAfterLast('.')
        return when (extension) {
            "pdf" -> "application/pdf"
            "txt" -> "text/plain"
            "md" -> "text/markdown"
            else -> "application/octet-stream"
        }
    }

    /**
     * 產生文件摘要
     * @param filePath 文件路徑
     * @return 文件摘要內容
     */
    suspend fun summarizeDocument(filePath: String): String {
        val response = client.execute(
            prompt = prompt("multimodel") {
                user {
                    text("請為這份文件提供簡潔的摘要，重點包括：")
                    text("1. 主要主題和目的")
                    text("2. 核心內容概述")
                    text("3. 重要結論或建議")
                    text("請用繁體中文回答，字數控制在 300 字以內。")

                    attachments {
                        binaryFile(Path(filePath), getMimeType(filePath))
                    }
                }
            },
            model = GoogleModels.Gemini2_5Flash
        )

        return response.first().content
    }

    /**
     * 提取文件關鍵要點
     * 從指定文件中提取 5-10 個最重要的關鍵要點，以條列式呈現
     * @param filePath 文件路徑，支援 PDF、TXT、MD 等格式
     * @return 格式化的關鍵要點列表字串
     */
    suspend fun extractKeyPoints(filePath: String): String {
        val response = client.execute(
            prompt = prompt("multimodel") {
                user {
                    text("請從這份文件中提取 5-10 個最重要的關鍵要點。")
                    text("請以條列式呈現，每個要點簡潔明確。")
                    text("格式：")
                    text("• 要點 1")
                    text("• 要點 2")
                    text("...")
                    attachments {
                        binaryFile(Path(filePath), getMimeType(filePath))
                    }
                }
            },
            model = GoogleModels.Gemini2_5Flash
        )

        return response.first().content
    }

    /**
     * 分析文件結構
     * 分析文件的組織方式、章節結構、資訊邏輯順序等
     * @param filePath 文件路徑，支援各種格式的結構化文件
     * @return 包含文件類型、結構分析的詳細描述
     */
    suspend fun analyzeStructure(filePath: String): String {
        val response = client.execute(
            prompt = prompt("multimodel") {
                user {
                    text("請分析這份文件的結構和組織方式，包括：")
                    text("1. 文件類型和性質")
                    text("2. 主要章節或段落結構")
                    text("3. 資訊的邏輯順序")
                    text("4. 是否包含圖表、表格等特殊元素")
                    attachments {
                        binaryFile(Path(filePath), getMimeType(filePath))
                    }
                }
            },
            model = GoogleModels.Gemini2_5Pro
        )

        return response.first().content
    }

    /**
     * 問答功能
     * 基於文件內容回答用戶提出的特定問題
     * @param filePath 作為知識來源的文件路徑
     * @param question 用戶的具體問題
     * @return 基於文件內容的 AI 回答，如無相關資訊會明確說明
     */
    suspend fun askQuestion(filePath: String, question: String): String {
        val response = client.execute(
            prompt = prompt("multimodel") {
                user {
                    text("請根據這份文件回答以下問題：")
                    text(question)
                    text("請確保回答基於文件內容，如果文件中沒有相關資訊，請明確說明。")
                    attachments {
                        binaryFile(Path(filePath), getMimeType(filePath))
                    }
                }
            },
            model = GoogleModels.Gemini2_5Flash
        )

        return response.first().content
    }
}