package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
    println("=== 🧮 計算專家 (溫度: 1, 迭代: 30) ===")
    val calculatorAgent = createOptimizedAgent("calculator")
    calculatorAgent.run("計算 25% 的 360 是多少？")

    println("\n=== 💼 商業顧問 (溫度: 0.4, 迭代: 40) ===")
    val advisorAgent = createOptimizedAgent("advisor")
    advisorAgent.run("小型餐廳如何增加客流量？")

    println("\n=== 🎨 創意夥伴 (溫度: 1.1, 迭代: 50) ===")
    val creativeAgent = createOptimizedAgent("creative")
    creativeAgent.run("為環保主題設計一個有趣的廣告標語")
}

data class AgentProfile(
    val name: String,
    val temperature: Double,
    val maxIterations: Int,
    val description: String
)

// 預設的 Agent 配置檔案
val agentProfiles = mapOf(
    "calculator" to AgentProfile(
        name = "計算專家",
        temperature = 0.1,
        maxIterations = 30,
        description = "精確計算，不需要複雜思考"
    ),
    "advisor" to AgentProfile(
        name = "商業顧問",
        temperature = 0.4,
        maxIterations = 40,
        description = "需要分析但保持專業"
    ),
    "creative" to AgentProfile(
        name = "創意夥伴",
        temperature = 1.1,
        maxIterations = 50,
        description = "發揮創意，允許深度發想"
    )
)

fun createOptimizedAgent(profileKey: String): AIAgent<String, String> {
    val profile = agentProfiles[profileKey]
        ?: error("未知的 Agent 類型: $profileKey")

    return AIAgent(
        executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!),
        toolRegistry = ToolRegistry {
            tool(SayToUser)
        },
        systemPrompt = """
            你是一個${profile.name}，專門提供${profile.description}的服務。
            請根據你的專業特性回應用戶的請求。
        """.trimIndent(),
        temperature = profile.temperature,
        maxIterations = profile.maxIterations,
        llmModel = OpenAIModels.CostOptimized.GPT4_1Mini
    )
}
