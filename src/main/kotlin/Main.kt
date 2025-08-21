package com.cashwu

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.structure.executeStructured
import ai.koog.prompt.structure.json.JsonSchemaGenerator
import ai.koog.prompt.structure.json.JsonStructuredData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
suspend fun main() {
// 建立自定義的 GPT-5 模型配置
    val customGPT5 = LLModel(
        provider = LLMProvider.OpenAI,
        id = "gpt-5-mini",
        capabilities = listOf(
            LLMCapability.Temperature,
            LLMCapability.Tools,
            LLMCapability.Schema.JSON.Simple,
            LLMCapability.PromptCaching,
            LLMCapability.Completion
        )
    )

    // 建立執行器
    val executor = simpleOpenAIExecutor(ApiKeyManager.openAIApiKey!!)

    // 產生結構化資料定義
    val weatherStructure = JsonStructuredData.createJsonStructure<WeatherForecast>(
        schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
        schemaType = JsonStructuredData.JsonSchemaType.SIMPLE,
    )

    // 執行結構化查詢
    val forecast = executor.executeStructured(
        prompt = prompt("weather-forecast") {
            system(
                """
                你是一位專業的天氣預報員。
                請根據用戶提供的城市，提供詳細的天氣預報。
                請確保所有數值都在合理範圍內。
                (為了測試需要，請給我相關的假天氣資料)
                """.trimIndent()
            )
            user("請提供台北市明天的天氣預報")
        },
        mainModel = customGPT5,
        structure = weatherStructure,
        retries = 3
    )

    // 處理結果
    forecast.fold(
        onSuccess = { response ->
            val weatherData = response.structure
            println("天氣預報資訊：")
            println("溫度：${weatherData.temperature}°C")
            println("天氣狀況：${weatherData.conditions}")
            println("降雨機率：${weatherData.rainChance}%")
            println("穿著建議：${weatherData.clothingRecommendation}")
            println("紫外線指數：${weatherData.uvIndex}")
        },
        onFailure = { error ->
            println("取得天氣預報失敗：${error.message}")
        }
    )
}

@Serializable
@SerialName("WeatherForecast")
data class WeatherForecast(
    @property:LLMDescription("攝氏溫度")
    val temperature: Int,

    @property:LLMDescription("天氣狀況描述，例如：晴朗、多雲、下雨")
    val conditions: String,

    @property:LLMDescription("降雨機率，範圍 0-100")
    val rainChance: Int,

    @property:LLMDescription("建議穿著")
    val clothingRecommendation: String,

    @property:LLMDescription("紫外線指數，範圍 0-11")
    val uvIndex: Int
)