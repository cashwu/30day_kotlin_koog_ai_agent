package com.cashwu

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val docProcessor = DocumentProcessingAgent()

    println("=== 自動檢查點功能演示 ===")

    val documentInput = "這是一份關於 AI 技術發展趨勢的研究報告草稿 (請模擬生成一份報告)"

    try {
        val result = docProcessor.processDocument(documentInput)
        println("\n🎯 處理結果：")
        println(result)
    } catch (e: Exception) {
        println("❌ 處理失敗：${e.message}")
    }
}