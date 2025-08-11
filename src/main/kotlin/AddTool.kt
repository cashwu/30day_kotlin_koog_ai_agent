package com.cashwu

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolArgs
import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.agents.core.tools.ToolParameterDescriptor
import ai.koog.agents.core.tools.ToolParameterType
import kotlinx.serialization.Serializable

/**
 *
 * @author cash.wu
 * @since 2025/08/11
 *
 */
object AddTool : SimpleTool<AddTool.Args>() {

    // 1. 定義參數類別
    @Serializable
    data class Args(val number1: Int, val number2: Int) : ToolArgs

    // 2. 設定序列化器
    override val argsSerializer = Args.serializer()

    // 3. 定義工具描述
    override val descriptor = ToolDescriptor(
        name = "add_numbers",
        description = "將兩個數字相加",
        requiredParameters = listOf(
            ToolParameterDescriptor(
                name = "number1",
                description = "第一個數字",
                type = ToolParameterType.Integer
            ),
            ToolParameterDescriptor(
                name = "number2",
                description = "第二個數字",
                type = ToolParameterType.Integer
            )
        )
    )

    // 4. 實作執行邏輯
    override suspend fun doExecute(args: Args): String {
        return try {
            val result = args.number1 + args.number2
            "計算結果：${args.number1} + ${args.number2} = $result"
        } catch (e: Exception) {
            "計算錯誤：${e.message}"
        }
    }
}