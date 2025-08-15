package com.cashwu

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    // 建立執行器
    val executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!)

    // 建立提示
    val prompt = prompt("streaming") {
        system("你是一個友善的 AI 助手，請使用正體中文回答問題")
        user("請簡單的說明，什麼是 Kotlin 的協程")
    }

    // 流式執行
    println("AI 正在回應...")

    with(SimpleStreamingMonitor()) {
        executor.executeStreaming(prompt, OpenAIModels.CostOptimized.GPT4_1Mini)
            .withPerformanceTracking()
            .collect { token ->
                // 即時輸出每個文字片段
                print(token)
            }
    }

    println("\n回應完成！")
}