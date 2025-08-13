package com.cashwu

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import kotlinx.io.files.Path

class ImageAnalyzer {

    class ImageAnalyzer(private val client: OpenAILLMClient) {

        /**
         * 判斷路徑是否為 URL
         */
        private fun isURL(path: String): Boolean {
            return path.startsWith("http://") || path.startsWith("https://")
        }

        /**
         * 基本圖像描述功能
         * 接收圖片路徑或 URL，返回詳細的圖像描述
         */
        suspend fun describeImage(
            imagePath: String,
            detailLevel: String = "詳細"
        ): String {
            val response = client.execute(
                prompt = prompt("multimodel") {
                    user {
                        text("請${detailLevel}地描述這張圖片的內容，包括：")
                        text("- 主要物件和人物")
                        text("- 場景和背景")
                        text("- 色彩和構圖")
                        text("- 整體氛圍和感受")

                        // 這是 Koog 的核心語法：直接在 prompt 中加入圖片
                        attachments {
                            if (isURL(imagePath)) {
                                image(imagePath) // 網路 URL 直接使用
                            } else {
                                image(Path(imagePath)) // 本地檔案路徑需使用 Path
                            }
                        }
                    }
                },
                // 使用支援視覺的模型
                model = OpenAIModels.CostOptimized.GPT4_1Mini
            )

            return response.first().content
        }

        /**
         * 圖像文字提取功能（OCR）
         * 從圖片中準確提取所有文字內容
         */
        suspend fun extractText(imagePath: String): String {
            val response = client.execute(
                prompt = prompt("multimodel") {
                    user {
                        text("請準確提取這張圖片中的所有文字內容。")
                        text("要求：")
                        text("1. 保持原有的格式和排版")
                        text("2. 區分不同的文字區塊")
                        text("3. 如果是表格，請用適當的格式呈現")
                        text("4. 如果看不清楚某些文字，請標註 [不清楚]")

                        // 這是 Koog 的核心語法：直接在 prompt 中加入圖片
                        attachments {
                            if (isURL(imagePath)) {
                                image(imagePath) // 網路 URL 直接使用
                            } else {
                                image(Path(imagePath)) // 本地檔案路徑需使用 Path
                            }
                        }
                    }
                },
                model = OpenAIModels.CostOptimized.GPT4_1Mini
            )

            return response.first().content
        }
    }
}