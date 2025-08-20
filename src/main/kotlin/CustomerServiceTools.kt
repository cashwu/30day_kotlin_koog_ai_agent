package com.cashwu

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import ai.koog.rag.base.mostRelevantDocuments
import ai.koog.rag.vector.EmbeddingBasedDocumentStorage
import java.nio.file.Files
import java.nio.file.Path

@LLMDescription("AI 客服工具，能夠搜尋公司知識庫回答客戶問題")
class CustomerServiceTools(
    private val documentStorage: EmbeddingBasedDocumentStorage<Path>
) : ToolSet {

    @Tool
    @LLMDescription("搜尋公司知識庫中與客戶問題相關的文件")
    suspend fun searchKnowledgeBase(
        @LLMDescription("客戶的問題或查詢內容")
        query: String,
        @LLMDescription("需要檢索的文件數量，預設為 3")
        count: Int = 3
    ): String {
        // 搜尋相關文件
        println("AI query document -- $query")
        val relevantDocs = documentStorage.mostRelevantDocuments(
            query = query,
            count = count,
            similarityThreshold = 0.5  // 適中的相似度闾值
        ).toList()

        if (relevantDocs.isEmpty()) {
            return "抱歉，在知識庫中找不到相關資訊。建議聯繫人工客服。"
        }

        // 整合搜尋結果
        val sb = StringBuilder("根據知識庫搜尋，找到以下相關資訊：\n\n")

        relevantDocs.forEachIndexed { index, document ->
            try {
                val content = Files.readString(document)
                sb.append("參考資料 ${index + 1}：${document.fileName}\n")
                sb.append("內容：$content\n\n")
            } catch (e: Exception) {
                println("警告：無法讀取文件 ${document.fileName}，原因：${e.message}")
                sb.append("參考資料 ${index + 1}：${document.fileName} (檔案讀取失敗)\n")
            }
        }

        return sb.toString()
    }
}