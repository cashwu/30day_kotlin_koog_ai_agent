package com.cashwu

import ai.koog.prompt.executor.clients.openai.OpenAILLMClient

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 建立 OpenAI 客戶端和圖像分析器
    val client = OpenAILLMClient(ApiKeyManager.openAIApiKey!!)
    val analyzer = ImageAnalyzer.ImageAnalyzer(client)

    println("=== Koog 圖像處理範例 ===\n")

    // 範例 1：描述一張風景照片
    println("1. 圖像內容描述")
    val description = analyzer.describeImage(
        imagePath = "https://images.pexels.com/photos/1172064/pexels-photo-1172064.jpeg",
        detailLevel = "簡潔"
    )
    println("圖片描述：$description\n")

    // 範例 2：從截圖中提取文字（如程式碼截圖）
    println("2. 文字提取（OCR）")
    val extractedText = analyzer.extractText(
        imagePath = "/Users/cash/Downloads/ocr.png"
    )
    println("提取的文字：\n$extractedText\n")
}
