package com.cashwu

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    println("🌟 OpenTelemetry 監控演示")
    println("=".repeat(50))

    val simpleMonitoring = SimpleMonitoring()

    // 執行一個會觸發 LLM 呼叫和工具執行的查詢
    val query = "今天台北的天氣如何？"

    println("📞 用戶查詢: $query")
    println()

    val result = simpleMonitoring.runWithMonitoring(query)

    println()
    println("🤖 Agent 回應: $result")
}