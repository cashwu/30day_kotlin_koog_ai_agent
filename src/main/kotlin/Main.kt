package com.cashwu

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val projectManager = ResearchPlanExecuteAgent()

    println("=== 研究-規劃-執行系統演示 ===\n")

    val projectDescription = """
        專案名稱：AI 驅動的客戶服務平台

        目標：
        - 開發一個智慧客服系統
        - 支援多語言對話
        - 整合現有 CRM 系統
        - 提供即時分析報告

        預算：200 萬台幣
        時程：12 個月
    """.trimIndent()

    println("📝 專案需求：")
    println(projectDescription)
    println("\n" + "=".repeat(60) + "\n")

    try {
        val result = projectManager.executeProject(projectDescription)
        println("🎯 專案管理結果：")
        println(result)
    } catch (e: Exception) {
        println("❌ 專案執行失敗：${e.message}")
    }
}