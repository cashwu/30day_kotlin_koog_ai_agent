package com.cashwu

import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 初始化 AI 客戶端
    val client = GoogleLLMClient(ApiKeyManager.googleApiKey!!)

    // 建立文件分析器
    val documentSummarizer = DocumentSummarizer(client)

    // 文件路徑範例
    val pdfPath = "/Users/cash/Downloads/file.pdf"
    val txtPath = "/Users/cash/Downloads/file.txt"
    val mdPath = "/Users/cash/Downloads/file.md"

    try {
        // 1. 產生 PDF 文件摘要
        println("=== PDF 文件摘要 ===")
        val pdfSummary = documentSummarizer.summarizeDocument(pdfPath)
        println(pdfSummary)

        // 2. 提取文字文件關鍵要點
        println("\n=== 文字文件關鍵要點 ===")
        val txtKeyPoints = documentSummarizer.extractKeyPoints(txtPath)
        println(txtKeyPoints)

        // 3. 分析 Markdown 文件結構
        println("\n=== Markdown 文件結構分析 ===")
        val mdStructure = documentSummarizer.analyzeStructure(mdPath)
        println(mdStructure)

        // 4. 針對文件提問
        println("\n=== 問答 ===")
        val answer = documentSummarizer.askQuestion(
            pdfPath,
            "跟 apple 有沒有什麼關係 ?"
        )
        println(answer)

    } catch (e: Exception) {
        println("處理文件時發生錯誤：${e.message}")
        e.printStackTrace()
    }
}
