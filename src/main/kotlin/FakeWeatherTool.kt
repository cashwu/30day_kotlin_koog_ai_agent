package com.cashwu

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolArgs
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

object FakeWeatherTool: SimpleTool<FakeWeatherTool.Args>() {
    @Serializable
    data class Args(val city: String) : ToolArgs

    override val argsSerializer = Args.serializer()

    override val descriptor = ToolDescriptor(
        name = "get_weather",
        description = "查詢指定城市的天氣狀況",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "city",
                description = "要查詢天氣的城市名稱",
                type = ToolParameterType.String
            )
        )
    )

    override suspend fun doExecute(args: Args): String {
        // 模擬 API 呼叫延遲
        delay(2000)

        return when (args.city.lowercase()) {
            "台北", "taipei" -> "台北今天晴朗，溫度 25°C，濕度 60%"
            "高雄", "kaohsiung" -> "高雄今天多雲，溫度 28°C，濕度 70%"
            else -> "${args.city} 今天天氣良好，溫度適中"
        }
    }
}