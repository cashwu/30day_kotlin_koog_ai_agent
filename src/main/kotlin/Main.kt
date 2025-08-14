package com.cashwu

import ai.koog.prompt.executor.clients.openai.OpenAILLMClient

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val client = OpenAILLMClient(ApiKeyManager.openAIApiKey!!)
    val meetingTranscriber = MeetingTranscriber(client)

    println("=== 會議記錄自動化範例 ===\n")

    try {
        val meetingRecord = meetingTranscriber.processMeetingAudio("/Users/cash.wu/Downloads/podcast.mp3")
//        val meetingRecord = meetingTranscriber.processMeetingAudio("/Users/cash/Downloads/team-meeting.wav")

        println("=== 會議記錄 ===")

        println("原始對話：\n${meetingRecord.transcription}")
        println("\n摘要：${meetingRecord.summary}")
        println("\n待辦事項：")
        meetingRecord.actionItems.forEach { item ->
            println("- $item")
        }

    } catch (e: Exception) {
        println("處理會議錄音失敗：${e.message}")
    }
}
