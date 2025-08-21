package com.cashwu

import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 建立 OpenAI 用戶端
    val client = OpenAILLMClient(ApiKeyManager.openAIApiKey!!)

    // 建立 embedder，使用 TextEmbedding3Small 模型
    val embedder = LLMEmbedder(client, OpenAIModels.Embeddings.TextEmbedding3Small)

    val index = ChunkedDocumentIndex(embedder)

    // 加入長文件（模擬）
    val longDocument = """
        人工智慧的發展歷程可以追溯到 1950 年代。艾倫·圖靈提出了著名的圖靈測試，
        這成為了判斷機器是否具有智慧的重要標準。

        在 1956 年的達特茅斯會議上，人工智慧這個術語首次被正式提出。約翰·麥卡錫、
        馬文·明斯基等學者奠定了 AI 研究的基礎。

        現代深度學習的興起始於 2006 年，傑佛瑞·辛頓等研究者的突破性工作讓神經網路
        重新受到關注。2012 年 AlexNet 在 ImageNet 競賽中的勝利標誌著深度學習時代的開始。

        今天，人工智慧已經應用到各個領域，從自動駕駛到語言翻譯，從醫療診斷到金融分析。
        ChatGPT 和 GPT-4 等大型語言模型的出現，更是讓 AI 技術走入了普通人的生活。
    """.trimIndent()

    index.addDocument("ai_history", longDocument)

    // 搜尋相關分塊
    val results = index.searchSimilarChunks("深度學習的發展", maxResults = 3)

    println("\n查詢：深度學習的發展")
    println("最相關的分塊：")
    results.forEach { (chunk, similarity) ->
        println("${chunk.id} (相似度: %.3f)".format(similarity))
        println("內容：${chunk.content.take(100)}...")
        println("-".repeat(50))
    }
}