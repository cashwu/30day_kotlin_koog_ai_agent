package com.cashwu

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

/**
 *
 * @author cash.wu
 * @since 2025/08/11
 *
 */
@LLMDescription("數學計算工具集")
class MathToolSet : ToolSet {

    @Tool
    @LLMDescription("將兩個數字相加")
    fun addNumbers(
        @LLMDescription("第一個數字")
        number1: Int,
        @LLMDescription("第二個數字")
        number2: Int
    ): String {
        return try {
            val result = number1 + number2
            "計算結果：$number1 + $number2 = $result"
        } catch (e: Exception) {
            "計算錯誤：${e.message}"
        }
    }

}