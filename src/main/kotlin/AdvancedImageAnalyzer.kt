package com.cashwu

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.message.Attachment
import ai.koog.prompt.message.AttachmentContent
import java.nio.file.Files
import kotlin.io.path.Path

class AdvancedImageAnalyzer(private val client: OpenAILLMClient) {

    /**
     * 使用 Attachment API 的進階圖像描述
     * 支援更多參數控制和圖像來源類型
     */
    suspend fun describeImageAdvanced(
        imagePath: String,
        fileName: String? = null,
        format: String = "jpg"
    ): String {
        val response = client.execute(
            prompt = prompt("multimodel"){
                user(
                    content = "請詳細描述這張圖片的內容",
                    attachments = listOf(
                        when {
                            imagePath.startsWith("http") -> {
                                // 網路 URL 圖片
                                Attachment.Image(
                                    content = AttachmentContent.URL(imagePath),
                                    format = format,
                                    fileName = fileName ?: "network_image.$format"
                                )
                            }
                            else -> {
                                // 本地檔案，讀取為 byte array
                                val imageBytes = Files.readAllBytes(Path(imagePath))
                                Attachment.Image(
                                    content = AttachmentContent.Binary.Bytes(imageBytes),
                                    format = format,
                                    fileName = fileName ?: Path(imagePath).fileName.toString()
                                )
                            }
                        }
                    )
                )
            },
            model = OpenAIModels.CostOptimized.GPT4_1Mini
        )

        return response.first().content
    }

    /**
     * 批次處理多張圖片
     */
    suspend fun batchImageAnalysis(
        imagePaths: List<String>,
        prompt: String = "比較這些圖片的異同點"
    ): String {
        val attachments = imagePaths.mapIndexed { index, path ->
            when {
                path.startsWith("http") -> {
                    Attachment.Image(
                        content = AttachmentContent.URL(path),
                        format = getImageFormat(path),
                        fileName = "image_$index.${getImageFormat(path)}"
                    )
                }
                else -> {
                    val imageBytes = Files.readAllBytes(Path(path))
                    Attachment.Image(
                        content = AttachmentContent.Binary.Bytes(imageBytes),
                        format = getImageFormat(path),
                        fileName = Path(path).fileName.toString()
                    )
                }
            }
        }

        val response = client.execute(
            prompt = prompt("multimodel") {
                user(
                    content = prompt,
                    attachments = attachments
                )
            },
            model = OpenAIModels.CostOptimized.GPT4_1Mini
        )

        return response.first().content
    }

    private fun getImageFormat(path: String): String {
        return path.substringAfterLast(".", "jpg").lowercase()
    }
}