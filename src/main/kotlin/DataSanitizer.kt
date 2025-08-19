package com.cashwu

/**
 *
 * @author cash.wu
 * @since 2025/08/19
 *
 */
class DataSanitizer {
    // 定義敏感資料的正則表達式模式
    private val sensitivePatterns = mapOf(
        "信用卡號" to Regex("\\b\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}\\b"),
        "電子郵件" to Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b")
    )

    /**
     * 檢查並屏蔽敏感資料
     */
    fun sanitize(text: String): SanitizationResult {
        var sanitizedText = text
        val detectedTypes = mutableListOf<String>()

        // 檢查每種敏感資料類型
        sensitivePatterns.forEach { (type, pattern) ->
            if (pattern.containsMatchIn(sanitizedText)) {
                detectedTypes.add(type)
                // 將敏感資料替換為 [已屏蔽]
                sanitizedText = sanitizedText.replace(pattern, "[已屏蔽]")
            }
        }

        return SanitizationResult(
            originalText = text,
            sanitizedText = sanitizedText,
            detectedTypes = detectedTypes,
            hasSensitiveData = detectedTypes.isNotEmpty()
        )
    }

    /**
     * 只檢查是否包含敏感資料，不進行替換
     */
    fun containsSensitiveData(text: String): Boolean {
        return sensitivePatterns.values.any { it.containsMatchIn(text) }
    }
}

// 遮罩結果資料類別
data class SanitizationResult(
    val originalText: String,       // 原始文字
    val sanitizedText: String,      // 遮罩後文字
    val detectedTypes: List<String>, // 檢測到的敏感資料類型
    val hasSensitiveData: Boolean   // 是否包含敏感資料
)