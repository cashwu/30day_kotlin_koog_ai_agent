package com.cashwu

import kotlinx.coroutines.delay

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val router = QueryRouter()

    println("🚀 QueryRouter 策略路由測試")
    println("=".repeat(50))

    // 測試案例
    val testQueries = listOf(
        "你們的營業時間是什麼？",                 // 簡單問題
        "我的產品無法正常運作，需要協助解決",        // 複雜問題
    )

    testQueries.forEachIndexed { index, query ->
        println("\n📋 測試 ${index + 1}: $query")

        val result = router.handleQuery(query)

        val strategyIcon = if (result.isComplex) "🤖" else "⚡"
        val strategyName = if (result.isComplex) "深度支援" else "快速回應"

        println("$strategyIcon 選擇策略: $strategyName")
        println("⏱️  處理時間: ${result.processingTimeMs}ms")
        println("💭 AI 回應: ${result.answer}")

        if (index < testQueries.size - 1) {
            println("=".repeat(50))
        }
    }

    println("\n✨ 測試完成！策略路由成功運作")

}
