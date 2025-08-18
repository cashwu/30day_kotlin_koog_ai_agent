package com.cashwu

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {

    val reliableAgent = DocumentProcessingAgentReliable()

    println("=== 故障恢復機制演示 ===")

    val documentInput = "這是一份關於 AI 技術發展趨勢的研究報告草稿 (請模擬生成一份報告)"

    repeat(5) { attempt ->
        println("\n🎯 第 ${attempt + 1} 次執行嘗試：")

        // 每次呼叫 processDocument 都會建立新的 Agent 實例
        // 新實例會自動從共享 storage 中的檢查點恢復狀態
        when (val result = reliableAgent.processDocument(documentInput)) {
            is ProcessResult.Success -> {
                println("✅ 任務完成：")
                println(result.content)
                return  // 成功後直接退出 main 函數
            }
            is ProcessResult.Failure -> {
                println("❌ 第 ${attempt + 1} 次嘗試失敗：${result.error}")
                // 下次重試將使用新的 Agent 實例，從檢查點繼續執行
            }
        }
    }

    println("\n⚠️ 已達到最大重試次數，任務終止")
}