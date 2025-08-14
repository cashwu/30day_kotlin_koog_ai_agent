package com.cashwu

import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    // 建立 OpenAI 客戶端和音訊轉錄器
    val client = OpenAILLMClient(ApiKeyManager.openAIApiKey!!)
    val transcriber = AudioTranscriber(client)

    println("=== Koog 音訊處理範例 ===\n")

    try {
        // 範例 1：處理本地音訊檔案
        println("1. 本地音訊檔案轉錄")
        val localTranscription = transcriber.transcribeAudio("/Users/cash.wu/Downloads/podcast.mp3")
//        val localTranscription = transcriber.transcribeAudio("/Users/cash/Downloads/podcast.mp3")
        println("轉錄結果：$localTranscription\n")

        // 範例 2：處理網路音訊檔案（如果有的話）
//         val urlTranscription = transcriber.transcribeAudio("https://example.com/audio.mp3")

    } catch (e: Exception) {
        println("轉錄失敗：${e.message}")
    }
}
