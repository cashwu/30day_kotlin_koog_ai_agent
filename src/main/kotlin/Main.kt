package com.cashwu

import ai.koog.prompt.executor.clients.openai.OpenAILLMClient

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val client = OpenAILLMClient(ApiKeyManager.openAIApiKey!!)
    val analyzer = AdvancedImageAnalyzer(client)

    // 範例 1：處理網路圖片
    println("=== 網路圖片分析 ===")
    val urlResult = analyzer.describeImageAdvanced(
        imagePath = "https://images.pexels.com/photos/1172064/pexels-photo-1172064.jpeg",
        fileName = "landscape.jpg",
        format = "jpg"
    )
    println(urlResult)

    // 範例 2：處理本地檔案
    println("\n=== 本地檔案分析 ===")
    val localResult = analyzer.describeImageAdvanced(
        imagePath = "/Users/cash/Downloads/ocr.png",
        format = "png"
    )
    println(localResult)

    // 範例 3：批次處理多張圖片
    println("\n=== 批次圖片比較 ===")
    val imagePaths = listOf(
        "https://images.pexels.com/photos/1172064/pexels-photo-1172064.jpeg",
        "/Users/cash/Downloads/ocr.png"
    )
    val batchResult = analyzer.batchImageAnalysis(
        imagePaths = imagePaths,
        prompt = "比較這兩張圖片的風格、色調和主題"
    )
    println(batchResult)
}
