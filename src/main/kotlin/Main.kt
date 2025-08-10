package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    val consultant = PirateConsultant()
    consultant.startConsultation()
}

class PirateConsultant {

    private val agent = AIAgent(
        executor = simpleOpenAIExecutor(System.getenv("OPENAI_API_KEY")),
        systemPrompt = """
            你是「程式海盜船長」，一個既專業又有趣的技術顧問：

            個性特質：
            - 用海盜的語調說話，但保持專業知識水準
            - 把程式概念比喻成航海和寶藏探險
            - 樂於分享技術知識，就像分享航海經驗
            - 使用正體中文，偶爾穿插「啊哈」、「船員」等詞彙

            專業領域：
            - Kotlin 程式設計
            - Android 開發
            - AI 應用開發
        """.trimIndent(),
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )

    suspend fun ask(question: String): String {
        return agent.run(question)
    }

    suspend fun startConsultation() {
        println("🏴‍☠️ 程式海盜船長上線！")
        println("啊哈！歡迎來到我的技術諮詢船艙，有什麼程式問題需要這位老船長指導的嗎？")
        println("輸入 'exit' 結束諮詢")
        println("-".repeat(50))

        while (true) {
            print("你的問題：")
            val input = readlnOrNull()?.trim()

            if (input.isNullOrEmpty()) continue
            if (input.lowercase() == "exit") {
                println("🏴‍☠️ 願程式的風永遠助你一臂之力，船員！再見！")
                break
            }

            try {
                val response = ask(input)
                println("🏴‍☠️ 船長回應：$response")
                println("-".repeat(50))
            } catch (e: Exception) {
                println("⚠️ 船遇到了風暴（錯誤）：${e.message}")
            }
        }
    }
}