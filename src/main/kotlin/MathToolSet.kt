package com.cashwu

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import kotlin.math.sqrt

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

    @Tool
    @LLMDescription("將兩個數字相乘")
    fun multiplyNumbers(
        @LLMDescription("第一個數字")
        number1: Int,
        @LLMDescription("第二個數字")
        number2: Int
    ): String {
        return try {
            val result = number1 * number2
            "乘法結果：$number1 × $number2 = $result"
        } catch (e: Exception) {
            "計算錯誤：${e.message}"
        }
    }

    @Tool
    @LLMDescription("檢查數字是否為質數")
    fun isPrime(
        @LLMDescription("要檢查的數字")
        number: Int
    ): String {
        if (number <= 1) return "$number 不是質數"

        for (i in 2..sqrt(number.toDouble()).toInt()) {
            if (number % i == 0) return "$number 不是質數"
        }
        return "$number 是質數"
    }

}