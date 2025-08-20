package com.cashwu

import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 建立 OpenAI 用戶端
    val client = OpenAILLMClient(ApiKeyManager.openAIApiKey!!)

    // 建立嵌入器，使用 TextEmbeddingAda002 模型
    val embedder = LLMEmbedder(client, OpenAIModels.Embeddings.TextEmbedding3Small)

    // 產生文字嵌入
    val text = "Kotlin 是一個現代的程式語言"
    val embedding = embedder.embed(text)

    println("文字：$text")
    println("向量維度：${embedding.dimension}")
    println("向量前 5 個值：${embedding.values.take(5)}")
}